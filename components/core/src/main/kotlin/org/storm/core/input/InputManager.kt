package org.storm.core.input

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles input, input history and input debouncing. Stores states of different inputs which occur and offers snapshot
 * functionality.
 */
class InputManager(
    private val debounceTime: Duration = Duration.ofMillis(200),
    private val maxInputHistory: Int = 5,
    private val maxInputDeadTimeMillis: Long = 8000L // time until we can safely drop
) {

    /**
     * Number of inputs since the last state snapshot was taken
     */
    var inputsSinceLastSnapshot: Int = 0
        private set

    /**
     * Historical record of inputs in order.
     */
    val inputHistory: List<Input> get() = _inputHistory

    private val inputMutex: Mutex = Mutex()

    private val activeInputs: MutableMap<String, Input> = ConcurrentHashMap()
    private val _inputHistory: MutableList<Input> = LinkedList<Input>() // want fast removal from head

    /**
     * Submits an action event to the manager. This will update the state of active actions, event history and action history.
     *
     * @param event The input event to submit
     */
    suspend fun processInput(event: InputEvent) {
        inputMutex.withLock {
            updateActiveInputs(event)

            activeInputs[event.type]?.let { input ->
                if (input.inDebounce) return@withLock

                updateInputHistory(input)
            }
        }
    }

    /**
     * Updates the current input state, removing dead inputs and incrementing active frames of non-dead inputs.
     *
     * @param currentTime current time in milliseconds
     */
    suspend fun updateInputState(currentTime: Double) {
        inputMutex.withLock {
            activeInputs.entries
                .forEach { (inputName, input) ->
                    if (currentTime - input.lastUpdateTime >= maxInputDeadTimeMillis) {
                        activeInputs.remove(inputName, input)
                    } else {
                        activeInputs[inputName] = input.copy(
                            activeFrames = input.activeFrames + 1
                        )
                    }
                }
        }
    }

    /**
     * Resets the state of the InputManager
     */
    suspend fun reset() {
        inputMutex.withLock {
            activeInputs.clear()
            _inputHistory.clear()
            inputsSinceLastSnapshot = 0
        }
    }

    /**
     * @return A snapshot of the current state of the InputManager
     */
    suspend fun getCurrentInputState(): InputState {
        return inputMutex.withLock {
            val inputState = InputState(
                activeInputs,
                _inputHistory,
                inputsSinceLastSnapshot
            )

            inputsSinceLastSnapshot = 0

            inputState
        }
    }

    /**
     * Updates the input history with the given input while the maintaining the max history length.
     *
     * @param input Input to add to history
     */
    private fun updateInputHistory(input: Input) {
        _inputHistory.add(input)

        if (_inputHistory.size > maxInputHistory) {
            _inputHistory.removeFirst()
        }

        inputsSinceLastSnapshot++
    }

    /**
     * Updates the active inputs based on the given input event
     *
     * @param event The input event to update the active input with
     */
    private fun updateActiveInputs(event: InputEvent) {
        if (!event.active) {
            activeInputs.remove(event.type)
            return
        }

        val currentTime = System.currentTimeMillis()

        activeInputs.computeIfPresent(event.type) { _, info ->
            val elapsedTime = currentTime - info.lastUpdateTime
            val inDebounce = elapsedTime < debounceTime.toMillis()

            info.copy(
                activations = if (inDebounce) info.activations + 1 else 1,
                inDebounce = inDebounce,
                timeHeld = info.timeHeld + elapsedTime,
                lastUpdateTime = currentTime
            )
        }

        activeInputs.putIfAbsent(
            event.type,
            Input(inDebounce = false, timeHeld = 0L, lastUpdateTime = currentTime, rawInput = event.input)
        )
    }
}