package org.storm.physics.math.geometry.shapes

import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Circle
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.extensions.getSquaredDistance
import org.storm.physics.math.extensions.toVector

class Circle(
    center: Point,
    radius: Double,
) : Circle(center, radius), CollidableShape {

    constructor(x: Double, y: Double, radius: Double) : this(Point(x, y), radius)

    override fun contains(p: Point): Boolean = this.center.getSquaredDistance(p) <= this.radius * this.radius

    override fun project(axis: Vector): Interval {
        val projectionLength = this.center.toVector().dot(axis)
        return Interval(projectionLength - radius, projectionLength + radius)
    }

    override fun toString(): String = "Circle(center = $center, radius: $radius)"

}
