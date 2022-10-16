package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.Vector;


@AllArgsConstructor
public class VelocityUpdateRequest implements Request {

  private final Entity entity;
  private final Vector velocity;

  @Override
  public void execute(StormEngine stormEngine) {
    this.entity.setVelocity(this.velocity);
  }
}
