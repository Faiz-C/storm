package org.storm.physics.constants;

import org.storm.physics.math.Vector;

/**
 * A Collections of helpful predefined Vectors
 */
public class Vectors {

  public static final Vector ZERO_VECTOR = new Vector(0, 0);
  public static final Vector UNIT_NORTH = new Vector(0, -1);
  public static final Vector UNIT_NORTH_EAST = new Vector(1, -1);
  public static final Vector UNIT_EAST = new Vector(1, 0);
  public static final Vector UNIT_SOUTH_EAST = new Vector(1, 1);
  public static final Vector UNIT_SOUTH = new Vector(0, 1);
  public static final Vector UNIT_SOUTH_WEST = new Vector(-1, 1);
  public static final Vector UNIT_WEST = new Vector(-1, 0);
  public static final Vector UNIT_NORTH_WEST = new Vector(-1, -1);

}
