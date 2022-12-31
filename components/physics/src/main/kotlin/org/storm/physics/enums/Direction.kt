package org.storm.physics.enums

import org.storm.physics.constants.Vectors
import org.storm.physics.math.Vector
import java.util.concurrent.ThreadLocalRandom

/**
 * Represents a direction in 2D space. There is 8 standard directions available in 2D space:
 * NORTH, NORTH EAST, EAST, SOUTH EAST, SOUTH, SOUTH WEST, WEST, NORTH WEST
 *
 * More precise directions must be created on a use case basis
 */
enum class Direction(
  val vector: Vector
) {

  NORTH(Vectors.UNIT_NORTH),
  NORTH_EAST(Vectors.UNIT_NORTH_EAST),
  EAST(Vectors.UNIT_EAST),
  SOUTH_EAST(Vectors.UNIT_SOUTH_EAST),
  SOUTH(Vectors.UNIT_SOUTH),
  SOUTH_WEST(Vectors.UNIT_SOUTH_WEST),
  WEST(Vectors.UNIT_WEST),
  NORTH_WEST(Vectors.UNIT_NORTH_WEST);

  companion object {
    fun random(): Direction {
      return when (ThreadLocalRandom.current().nextInt(0, 8)) {
        0 -> NORTH
        1 -> NORTH_EAST
        2 -> EAST
        3 -> SOUTH_EAST
        4 -> SOUTH
        5 -> SOUTH_WEST
        6 -> WEST
        else -> NORTH_WEST
      }
    }
  }
}
