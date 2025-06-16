package org.storm.engine.example

import org.storm.core.context.Context
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
import kotlin.collections.contains

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
            }.mapKeys {
                Controls.BINDINGS[it.key]!!
            }

        return ActionState(activeActions)
    }

}
