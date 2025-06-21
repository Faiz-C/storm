package org.storm.core.input

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.storm.core.utils.toMilliseconds
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

/**
 * Handles input, input history and input debouncing. Stores states of different inputs which occur and offers snapshot
 * functionality.
 */
class InputManager(
    private val debounceTime: Duration = Duration.ofMillis(200),
    private val maxInputHistory: Int = 5,
    private val maxInputDeadTimeMillis: Double = 1000.0, // time until we can safely drop
    private val maxTrackedActivations: Int = 500, // 500 consecutive activations
    private val maxTrackedFrames: Int = 100, // 100 consecutive frames
    private val maxTrackedHoldTimeMillis: Double = 60000.0 // 1m
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
                            activeFrames = min(input.activeFrames + 1, maxTrackedFrames)
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

        val currentTime = toMilliseconds(System.nanoTime())

        activeInputs.computeIfPresent(event.type) { _, info ->
            val elapsedTime = currentTime - info.lastUpdateTime
            val inDebounce = elapsedTime < debounceTime.toMillis()

            info.copy(
                activations = if (inDebounce) min(info.activations + 1, maxTrackedActivations) else 1,
                inDebounce = inDebounce,
                timeHeld = min(info.timeHeld + elapsedTime, maxTrackedHoldTimeMillis),
                lastUpdateTime = currentTime,
                rawInput = event.input
            )
        }

        activeInputs.putIfAbsent(
            event.type,
            Input(inDebounce = false, timeHeld = 0.0, lastUpdateTime = currentTime, rawInput = event.input)
        )
    }
}