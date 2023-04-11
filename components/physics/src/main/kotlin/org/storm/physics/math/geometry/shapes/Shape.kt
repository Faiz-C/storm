package org.storm.physics.math.geometry.shapes

import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.Geometric
import org.storm.physics.math.geometry.LineSegment
import org.storm.physics.math.geometry.Point
import org.storm.physics.transforms.TransformableRender

/**
 * A Shape is a geometric representation of a (common) shape in Euclidean (2D) space
 */
interface Shape : Geometric, TransformableRender {

  /**
   * @return the edges of the shape as LineSegments if applicable
   */
  val edges: List<LineSegment>

  /**
   * @return the center of the Shape if applicable
   */
  val center: Point

  /**
   * Rotates this Shape around the given Point by the given angle *anticlockwise*.
   *
   * @param point Point to rotate around
   * @param angle angle in radians to rotate by
   */
  fun rotate(point: Point, angle: Double)

  /**
   * Calculates and returns the projection of the shape onto the given axis (in vector form). This
   * is used for collision detection.
   *
   * @param axis axis to project onto
   * @return an Interval representing the projection
   */
  fun project(axis: Vector): Interval

  /**
   * @param p Point to check
   * @return true if the given Point is contained within the shape
   */
  operator fun contains(p: Point): Boolean

}
