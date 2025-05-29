package org.storm.physics.math.extensions

import org.storm.core.graphics.geometry.Point
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.Line

/**
 * @param p Point to calculate distance to
 * @return distance from this Point to the given Point p
 */
fun Point.getDistance(p: Point): Double = Line(this, p).length

/**
 * @param p Point to calculate squared distance to
 * @return squared distance from this Point to the given Point p
 */
fun Point.getSquaredDistance(p: Point): Double = Line(this, p).squaredLength

/**
 * @return this Point as a Vector from the Origin
 */
fun Point.toVector(): Vector = Vector(this.x, this.y)