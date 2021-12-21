package org.storm.engine.example;

import org.storm.physics.entity.ImmovableEntity;
import org.storm.physics.math.geometry.shapes.Shape;

public class ImmovableEntityImpl extends ImmovableEntity {

  public ImmovableEntityImpl(double x, double y, Shape hurtBox) {
    super(x, y, hurtBox);
  }

}
