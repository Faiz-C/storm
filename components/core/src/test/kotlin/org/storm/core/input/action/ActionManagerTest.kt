package org.storm.core.input.action

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class ActionManagerTest {

    private val actionManager = ActionManager()

    @BeforeEach
    fun setup() = runBlocking {
        actionManager.reset()
    }

    @Test
    fun testSubmitEvents() = runBlocking {
        actionManager.submitActionEvent(ActionEvent("up", true))

        actionManager.updateActiveFrames()
        val state = actionManager.getStateSnapshot()

        assert(state.isFirstActivation("up")) {
            "Expected exactly 1 activation and 1 active frame for the 'up' action"
        }

        actionManager.submitActionEvent(ActionEvent("up", false))

        actionManager.updateActiveFrames()
        val stateAfter = actionManager.getStateSnapshot()

        assert(!stateAfter.activeActions.containsKey("up")) {
            "Expected 'up' to not be activated"
        }

        assert(stateAfter.activeActionHistory.contains("up")) {
            "Expected 'up' to be in the action history"
        }
    }

    @Test
    fun testDebounce() = runBlocking {
        actionManager.submitActionEvent(ActionEvent("up", true))
        actionManager.submitActionEvent(ActionEvent("up", true))
        actionManager.submitActionEvent(ActionEvent("up", true))
        actionManager.submitActionEvent(ActionEvent("up", true))

        val state = actionManager.getStateSnapshot()

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

        val actionManagerWithoutDebounce = ActionManager(debounceTime = Duration.ofMillis(0))

        actionManagerWithoutDebounce.submitActionEvent(ActionEvent("up", true))
        actionManagerWithoutDebounce.submitActionEvent(ActionEvent("up", true))

        actionManagerWithoutDebounce.updateActiveFrames()
        val withoutDebounceState = actionManagerWithoutDebounce.getStateSnapshot()

        assert(withoutDebounceState.activeActions["up"]?.activations == 1) {
            "Expected 'up' to have 1 activation when there is no debounce"
        }
    }

    @Test
    fun testActionCombinations() = runBlocking {
        actionManager.submitActionEvent(ActionEvent("move-right", true))
        actionManager.submitActionEvent(ActionEvent("jump", true))

        actionManager.updateActiveFrames()
        val state = actionManager.getStateSnapshot()

        assert(state.isCombinationPresent(listOf("move-right", "jump"))) {
            "Expected both actions to be active and present"
        }
    }
}