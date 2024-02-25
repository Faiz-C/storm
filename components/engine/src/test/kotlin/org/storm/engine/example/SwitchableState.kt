package org.storm.engine.example

import org.storm.core.input.ActionState
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.StateChangeRequest
import org.storm.engine.state.State

abstract class SwitchableState : State() {

    override suspend fun process(actionState: ActionState, requestQueue: RequestQueue) {
        if (actionState.isFirstTrigger(KeyActionConstants.ONE)) {
            requestQueue.submit(StateChangeRequest(KeyActionConstants.ONE))
        } else if (actionState.isFirstTrigger(KeyActionConstants.TWO)) {
            requestQueue.submit(StateChangeRequest(KeyActionConstants.TWO))
        } else if (actionState.isFirstTrigger(KeyActionConstants.THREE)) {
            requestQueue.submit(StateChangeRequest(KeyActionConstants.THREE))
        } else if (actionState.isFirstTrigger(KeyActionConstants.FOUR)) {
            requestQueue.submit(StateChangeRequest(KeyActionConstants.FOUR))
        } else if (actionState.isFirstTrigger(KeyActionConstants.FIVE)) {
            requestQueue.submit(StateChangeRequest(KeyActionConstants.FIVE))
        }
    }

}
