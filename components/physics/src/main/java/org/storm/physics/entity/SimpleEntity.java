package org.storm.physics.entity;

import lombok.NonNull;
import org.storm.physics.math.geometry.shapes.Shape;

public class SimpleEntity extends Entity {

  public SimpleEntity(@NonNull Shape hurtBox, double speed, double mass, double restitution) {
    super(hurtBox, speed, mass, restitution);
  }

}
