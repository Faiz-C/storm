package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;
import org.storm.physics.entity.Entity;

@AllArgsConstructor
public class ClearForcesRequest implements Request {

  private final Entity entity;

  @Override
  public void execute(StormEngine stormEngine) {
    this.entity.clearForces();
  }

}
