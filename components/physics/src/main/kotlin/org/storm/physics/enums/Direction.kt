package org.storm.physics.enums

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

    NORTH(Vector.UNIT_NORTH),
    NORTH_EAST(Vector.UNIT_NORTH_EAST),
    EAST(Vector.UNIT_EAST),
    SOUTH_EAST(Vector.UNIT_SOUTH_EAST),
    SOUTH(Vector.UNIT_SOUTH),
    SOUTH_WEST(Vector.UNIT_SOUTH_WEST),
    WEST(Vector.UNIT_WEST),
    NORTH_WEST(Vector.UNIT_NORTH_WEST);

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
