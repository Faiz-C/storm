package org.storm.physics.constants

import org.storm.physics.math.Vector

/**
 * A Collections of helpful predefined Vectors
 */
object Vectors {
  @JvmField
  val ZERO_VECTOR = Vector(0.0, 0.0)

  @JvmField
  val UNIT_NORTH = Vector(0.0, -1.0)

  @JvmField
  val UNIT_NORTH_EAST = Vector(1.0, -1.0)

  @JvmField
  val UNIT_EAST = Vector(1.0, 0.0)

  @JvmField
  val UNIT_SOUTH_EAST = Vector(1.0, 1.0)

  @JvmField
  val UNIT_SOUTH = Vector(0.0, 1.0)

  @JvmField
  val UNIT_SOUTH_WEST = Vector(-1.0, 1.0)

  @JvmField
  val UNIT_WEST = Vector(-1.0, 0.0)

  @JvmField
  val UNIT_NORTH_WEST = Vector(-1.0, -1.0)
}
