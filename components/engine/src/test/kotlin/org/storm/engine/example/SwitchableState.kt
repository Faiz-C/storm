package org.storm.engine.example

import org.storm.core.input.action.ActionManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.StateChangeRequest
import org.storm.engine.state.State

abstract class SwitchableState : State() {

  override fun process(actionManager: ActionManager, requestQueue: RequestQueue) {
    if (actionManager.isPerforming(KeyActionConstants.ONE)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.ONE))
    } else if (actionManager.isPerforming(KeyActionConstants.TWO)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.TWO))
    } else if (actionManager.isPerforming(KeyActionConstants.THREE)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.THREE))
    } else if (actionManager.isPerforming(KeyActionConstants.FOUR)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.FOUR))
    } else if (actionManager.isPerforming(KeyActionConstants.FIVE)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.FIVE))
    }
  }

}
