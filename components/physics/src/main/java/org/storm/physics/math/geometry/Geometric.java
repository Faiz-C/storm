package org.storm.physics.math.geometry;

/**
 * A Geometric object can be translated (moved) in Euclidean (2D) space
 */
public interface Geometric {

  /**
   * Translates (moves) the Geometric object by the given deltas
   *
   * @param dx delta in the x-axis
   * @param dy delta in the y-axis
   */
  void translate(double dx, double dy);

}