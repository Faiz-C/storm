package org.storm.engine.example

import org.storm.core.input.Action
import org.storm.core.input.ActionState
import org.storm.core.input.InputState
import org.storm.engine.Controls
import org.storm.engine.Controls.FIVE
import org.storm.engine.Controls.FOUR
import org.storm.engine.Controls.ONE
import org.storm.engine.Controls.THREE
import org.storm.engine.Controls.TWO
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.StateChangeRequest
import org.storm.engine.state.GameState

abstract class SwitchableState : GameState {

    override suspend fun process(actionState: ActionState) {
        if (actionState.isFirstActivation(ONE)) {
            RequestQueue.submit(StateChangeRequest(ONE))
        } else if (actionState.isFirstActivation(TWO)) {
            RequestQueue.submit(StateChangeRequest(TWO))
        } else if (actionState.isFirstActivation(THREE)) {
            RequestQueue.submit(StateChangeRequest(THREE))
        } else if (actionState.isFirstActivation(FOUR)) {
            RequestQueue.submit(StateChangeRequest(FOUR))
        } else if (actionState.isFirstActivation(FIVE)) {
            RequestQueue.submit(StateChangeRequest(FIVE))
        }
    }

    override fun getActionState(inputState: InputState): ActionState {
        val activeActions = inputState.activeInputs
            .filterKeys {
                Controls.BINDINGS.contains(it)
            }.map { (key, value) ->
                Controls.BINDINGS[key]!! to Action(value)
            }.toMap()

        // Just for validating mouse inputs
        //inputState.activeInputs["mouse-moved"]?.let {
        //    val input = it.rawInput as MouseEvent
        //    val pos = Point(input.x, input.y)
        //    println("Mouse position: $pos, ${it.lastUpdateTime}")
        //}

        return ActionState(activeActions)
    }

}
