package org.storm.engine.example;

import org.storm.physics.entity.Entity;
import org.storm.physics.math.geometry.shapes.Shape;

public class EntityImpl extends Entity {

  public EntityImpl(double x, double y, Shape hurtBox, double mass, double restitution) {
    super(x, y, hurtBox, mass, restitution);
  }

}
