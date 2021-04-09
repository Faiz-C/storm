package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;

/**
 * A Request which changes the current state of the StormEngine.
 */
@AllArgsConstructor
public class StateChangeRequest implements Request {

  private final String stateId;

  @Override
  public void execute(StormEngine stormEngine) {
    stormEngine.swapState(this.stateId);
  }

}
