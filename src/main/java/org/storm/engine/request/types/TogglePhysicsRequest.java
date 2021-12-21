package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;

/**
 * A Request which pauses or unpauses the PhysicsEngine.
 */
@AllArgsConstructor
public class TogglePhysicsRequest implements Request {

  private final boolean paused;

  @Override
  public void execute(StormEngine stormEngine) {
    stormEngine.getPhysicsEngine().setPaused(this.paused);
  }

}
