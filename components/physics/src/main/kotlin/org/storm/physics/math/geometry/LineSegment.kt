package org.storm.physics.math.geometry

import org.apache.commons.math3.util.FastMath
import org.storm.physics.math.Vector

/**
 * Represents a mathematical line segment in 2D space.
 */
class LineSegment(
  start: Point,
  end: Point
) : Geometric {

  val start: Point = start.copy()
  val end: Point = end.copy()
  val vectorFromStart: Vector = Vector(this.start, this.end)
  val vectorFromEnd: Vector = Vector(this.end, this.start)

  // The squared length of this LineSegment
  val squaredLength: Double = run {
    val xDiff = end.x - start.x
    val yDiff = end.y - start.y
    xDiff * xDiff + yDiff * yDiff
  }

  val length: Double = FastMath.sqrt(this.squaredLength)

  constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this(Point(x1, y1), Point(x2, y2))

  /**
   * @param p Point to check
   * @return Point on this LineSegment closest to p
   */
  fun getClosestPoint(p: Point): Point {
    val startToP = Vector(start, p)
    val (x, y) = this.vectorFromStart.projection(startToP)
    return Point(this.start.x + x, this.start.y + y)
  }

  /**
   * @param p Point to check
   * @return true if p is on the LineSegment, false otherwise
   */
  operator fun contains(p: Point): Boolean {
    return this.start.getDistance(p) + p.getDistance(end) == this.length
  }

  override fun translate(dx: Double, dy: Double) {
    this.start.translate(dx, dy)
    this.end.translate(dx, dy)
  }

}
