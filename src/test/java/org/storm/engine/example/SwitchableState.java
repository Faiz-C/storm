package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.types.StateChangeRequest;
import org.storm.engine.state.State;

public abstract class SwitchableState extends State {

  public SwitchableState() {
    super();
  }

  @Override
  public void process(ActionManager actionManager, RequestQueue requestQueue) {
    if (actionManager.isPerforming(KeyActionConstants.ONE)) {
      requestQueue.submit(new StateChangeRequest(KeyActionConstants.ONE));
    } else if (actionManager.isPerforming(KeyActionConstants.TWO)) {
      requestQueue.submit(new StateChangeRequest(KeyActionConstants.TWO));
    } else if (actionManager.isPerforming(KeyActionConstants.THREE)) {
      requestQueue.submit(new StateChangeRequest(KeyActionConstants.THREE));
    } else if (actionManager.isPerforming(KeyActionConstants.FOUR)) {
      requestQueue.submit(new StateChangeRequest(KeyActionConstants.FOUR));
    } else if (actionManager.isPerforming(KeyActionConstants.FIVE)) {
      requestQueue.submit(new StateChangeRequest(KeyActionConstants.FIVE));
    }
  }

}
