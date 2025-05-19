package org.storm.physics.math.extensions

import org.apache.commons.math3.util.FastMath
import org.storm.core.graphics.geometry.Point
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.LineSegment

/**
 * @param p Point to calculate distance to
 * @return distance from this Point to the given Point p
 */
fun Point.getDistance(p: Point): Double = LineSegment(this, p).length

/**
 * @param p Point to calculate squared distance to
 * @return squared distance from this Point to the given Point p
 */
fun Point.getSquaredDistance(p: Point): Double = LineSegment(this, p).squaredLength

/**
 * @return this Point as a Vector from the Origin
 */
fun Point.toVector(): Vector = Vector(this.x, this.y)

/**
 * Rotates this Point around the given Point by the given angle *anticlockwise*
 *
 * @param point Point to rotate around
 * @param angle angle in radians to rotate by
 */
fun Point.rotate(point: Point, angle: Double) {
    this.x = FastMath.cos(angle) * (x - point.x) - FastMath.sin(angle) * (y - point.y) + point.x
    this.y = FastMath.sin(angle) * (x - point.x) + FastMath.cos(angle) * (y - point.y) + point.y
}