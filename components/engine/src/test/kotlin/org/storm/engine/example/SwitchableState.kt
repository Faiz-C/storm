package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.input.action.ActionState
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.StateChangeRequest
import org.storm.engine.state.GameState

abstract class SwitchableState : GameState {

    override suspend fun process(actionState: ActionState) {
        if (actionState.isFirstActivation(KeyActionConstants.ONE)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.ONE))
        } else if (actionState.isFirstActivation(KeyActionConstants.TWO)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.TWO))
        } else if (actionState.isFirstActivation(KeyActionConstants.THREE)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.THREE))
        } else if (actionState.isFirstActivation(KeyActionConstants.FOUR)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.FOUR))
        } else if (actionState.isFirstActivation(KeyActionConstants.FIVE)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.FIVE))
        }
    }

}
