package org.storm.physics.entity;

import lombok.NonNull;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.shapes.Shape;

/**
 * An ImmovableEntity is a normal Entity but cannot be moved.
 */
public class ImmovableEntity extends Entity {

  public ImmovableEntity(@NonNull Shape hurtBox) {
    super(hurtBox, 0, Double.POSITIVE_INFINITY, 1);
  }

  @Override
  public void translate(Vector vector) {
    // ImmovableEntity does not move
  }

  @Override
  public void translate(double dx, double dy) {
    // ImmovableEntity does not move
  }

}
