package org.storm.physics.collision;

import org.storm.physics.constants.Vectors;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.shapes.Shape;

/**
 * The CollisionDetector is useful for checking whether two given closed shapes (in 2D) have collided. It also has the ability
 * to determine the Minimum Translation Vector to allow for reactions upon colliding.
 *
 * Currently Supported:
 *
 * Convex Polygon x Convex Polygon
 * Circle x Convex Polygon
 * Circle x Circle
 */
public class CollisionDetector {

  private CollisionDetector() {}

  public static boolean check(Shape s1, Shape s2) {
    return checkMtv(s1, s2) != Vectors.ZERO_VECTOR;
  }

  public static Vector checkMtv(Shape s1, Shape s2) {
    // Both circles, need to treated differently
    return (s1.getEdges().isEmpty() && s2.getEdges().isEmpty()) ? MtvCalculator.calcCircleMtv((Circle) s1, (Circle) s2) : MtvCalculator.calcMtv(s1, s2);
  }

}
