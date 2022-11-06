package org.storm.physics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.storm.physics.constants.Vectors;
import org.storm.physics.math.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a direction in 2D space. There is 8 standard directions available in 2D space:
 * NORTH, NORTH EAST, EAST, SOUTH EAST, SOUTH, SOUTH WEST, WEST, NORTH WEST
 *
 * More precise directions must be created on a use case basis
 */
@Getter
@AllArgsConstructor
public enum Direction {
  NORTH(Vectors.UNIT_NORTH),
  NORTH_EAST(Vectors.UNIT_NORTH_EAST),
  EAST(Vectors.UNIT_EAST),
  SOUTH_EAST(Vectors.UNIT_SOUTH_EAST),
  SOUTH(Vectors.UNIT_SOUTH),
  SOUTH_WEST(Vectors.UNIT_SOUTH_WEST),
  WEST(Vectors.UNIT_WEST),
  NORTH_WEST(Vectors.UNIT_NORTH_WEST);

  private final Vector vector;

  public static Direction random() {
    switch (ThreadLocalRandom.current().nextInt(0, 8)) {
      case 0:
        return NORTH;
      case 1:
        return NORTH_EAST;
      case 2:
        return EAST;
      case 3:
        return SOUTH_EAST;
      case 4:
        return SOUTH;
      case 5:
        return SOUTH_WEST;
      case 6:
        return WEST;
      default:
        return NORTH_WEST;
    }
  }
}
