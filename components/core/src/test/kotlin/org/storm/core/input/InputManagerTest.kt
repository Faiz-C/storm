package org.storm.core.input

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class InputManagerTest {

    private val inputManager = InputManager(maxInputDeadTimeMillis = 1000)

    private val inputTranslator = object: InputTranslator {

        private val bindings = mapOf(
            "w" to "up",
            "space" to "jump",
            "d" to "right"
        )

        override fun getActionState(inputState: InputState): ActionState {
            val activeActions = inputState.activeInputs
                .filterKeys {
                    bindings.contains(it)
                }.mapKeys {
                    bindings[it.key]!!
                }

            return ActionState(activeActions)
        }
    }

    @BeforeEach
    fun setup() = runBlocking {
        inputManager.reset()
    }

    @Test
    fun testProcessInputs() = runBlocking {
        inputManager.processInput(InputEvent("w", "test"))

        inputManager.updateInputState(System.currentTimeMillis().toDouble())
        val actionState = inputTranslator.getActionState(inputManager.getCurrentInputState())

        assert(actionState.isFirstActivation("up")) {
            "Expected exactly 1 activation and 1 active frame for the 'up' action"
        }

        // Simulate 1 second passing without this same input being triggered, it should be cleaned up as it's considered
        // dead now
        inputManager.updateInputState(System.currentTimeMillis().toDouble() + 2000)
        val inputStateAfter = inputManager.getCurrentInputState()
        val actionStateAfter = inputTranslator.getActionState(inputStateAfter)

        assert(!actionStateAfter.activeActions.containsKey("up")) {
            "Expected 'up' to not be activated"
        }

        assert(inputStateAfter.inputHistory.find { it.rawInput == "test" } != null) {
            "Expected 'test' to be in the input history"
        }
    }

    @Test
    fun testDebounce() = runBlocking {
        inputManager.processInput(InputEvent("w", "test"))
        inputManager.processInput(InputEvent("w", "test"))
        inputManager.processInput(InputEvent("w", "test2"))
        inputManager.processInput(InputEvent("w", "test4"))

        inputManager.updateInputState(System.currentTimeMillis().toDouble())
        val state = inputTranslator.getActionState(inputManager.getCurrentInputState())

        assert(state.activeActions.containsKey("up")) {
            "Expected 'up' to be an active action"
        }

        assert(state.activeActions["up"]?.inDebounce == true) {
            "Expected 'up' to be in debounce"
        }

        // Testing the tracking of activations during the debounce window
        assert(state.activeActions["up"]?.activations == 4) {
            "Expected 'up' to have 4 activations since the action is in debounce"
        }

        assert(state.activeActions["up"]?.rawInput == "test4") {
            "Expected 'up' action's raw input to be updated to test4 instead of test"
        }

        val inputManagerWithoutDebounce = InputManager(debounceTime = Duration.ofMillis(0))

        inputManagerWithoutDebounce.processInput(InputEvent("w", "test"))
        inputManagerWithoutDebounce.processInput(InputEvent("w", "test"))

        inputManager.updateInputState(System.currentTimeMillis().toDouble())
        val withoutDebounceState = inputTranslator.getActionState(inputManagerWithoutDebounce.getCurrentInputState())

        assert(withoutDebounceState.activeActions["up"]?.activations == 1) {
            "Expected 'up' to have 1 activation when there is no debounce"
        }
    }

    @Test
    fun testActionCombinations() = runBlocking {
        inputManager.processInput(InputEvent("d", "test"))
        inputManager.processInput(InputEvent("space", "test"))

        inputManager.updateInputState(System.currentTimeMillis().toDouble())
        val state = inputTranslator.getActionState(inputManager.getCurrentInputState())

        assert(state.isCombinationPresent(listOf("right", "jump"))) {
            "Expected both actions to be active and present"
        }
    }
}