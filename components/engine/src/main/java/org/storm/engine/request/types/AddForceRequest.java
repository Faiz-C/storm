package org.storm.engine.request.types;

import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.Vector;

/**
 * A Request which adds a force to an Entity
 */
public class AddForceRequest implements Request {

  private final Entity entity;
  private final Vector force;
  private final double duration;

  public AddForceRequest(Entity entity, Vector force, double duration) {
    this.entity = entity;
    this.force = force;
    this.duration = duration;
  }

  public AddForceRequest(Entity entity, Vector force) {
    this(entity, force, Double.POSITIVE_INFINITY);
  }

  @Override
  public void execute(StormEngine stormEngine) {
    if (duration == Double.POSITIVE_INFINITY) {
      this.entity.addForce(this.force);
    } else {
      this.entity.addForce(this.force, this.duration);
    }
  }

}
