package org.storm.engine.example

import org.storm.core.input.action.ActionManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.StateChangeRequest
import org.storm.engine.state.State

abstract class SwitchableState : State() {

  override fun process(actionManager: ActionManager, requestQueue: RequestQueue) {
    if (actionManager.isActive(KeyActionConstants.ONE)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.ONE))
    } else if (actionManager.isActive(KeyActionConstants.TWO)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.TWO))
    } else if (actionManager.isActive(KeyActionConstants.THREE)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.THREE))
    } else if (actionManager.isActive(KeyActionConstants.FOUR)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.FOUR))
    } else if (actionManager.isActive(KeyActionConstants.FIVE)) {
      requestQueue.submit(StateChangeRequest(KeyActionConstants.FIVE))
    }
  }

}
