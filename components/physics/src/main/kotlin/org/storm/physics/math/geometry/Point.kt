package org.storm.physics.math.geometry

import org.apache.commons.math3.util.FastMath
import org.storm.physics.math.Vector

/**
 * A Point represents a mathematical point in 2D space. The x and y values of a Point are arbitrary *unit* values for
 * the 2D space. They may not be 1:1 with pixels on the screen.
 */
data class Point(
  var x: Double,
  var y: Double
) : Geometric {

  /**
   * @param p Point to calculate distance to
   * @return distance from this Point to the given Point p
   */
  fun getDistance(p: Point): Double = LineSegment(this, p).length

  /**
   * @param p Point to calculate squared distance to
   * @return squared distance from this Point to the given Point p
   */
  fun getSquaredDistance(p: Point): Double = LineSegment(this, p).squaredLength

  /**
   * @return this Point as a Vector from the Origin
   */
  fun toVector(): Vector = Vector(this.x, this.y)

  /**
   * Rotates this Point around the given Point by the given angle
   *
   * @param point Point to rotate around
   * @param angle angle in radians to rotate by
   */
  fun rotate(point: Point, angle: Double) {
    this.x = FastMath.cos(angle) * (x - point.x) - FastMath.sin(angle) * (y - point.y) + point.x
    this.y = FastMath.sin(angle) * (x - point.x) + FastMath.cos(angle) * (y - point.y) + point.y
  }

  override fun translate(dx: Double, dy: Double) {
    this.x += dx
    this.y += dy
  }

}
