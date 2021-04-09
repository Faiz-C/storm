package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.Vector;

/**
 * A Request which removes a force from an Entity
 */
@AllArgsConstructor
public class RemoveForceRequest implements Request {

  private final Entity entity;

  private final Vector force;

  @Override
  public void execute(StormEngine stormEngine) {
    stormEngine.getPhysicsEngine().removeForce(this.entity, this.force);
  }

}
