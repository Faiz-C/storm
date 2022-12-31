package org.storm.physics.entity

import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.Shape

/**
 * An ImmovableEntity is a normal Entity but cannot be moved.
 */
open class ImmovableEntity(hurtBox: Shape) : Entity(hurtBox, 0.0, Double.POSITIVE_INFINITY, 1.0) {

  override fun translate(vector: Vector) {
    // ImmovableEntity does not move
  }

  override fun translate(dx: Double, dy: Double) {
    // ImmovableEntity does not move
  }

}
