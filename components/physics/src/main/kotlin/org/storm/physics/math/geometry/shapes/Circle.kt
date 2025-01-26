package org.storm.physics.math.geometry.shapes

import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Settings
import org.storm.core.render.geometry.Point
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.extensions.getSquaredDistance
import org.storm.physics.math.extensions.rotate
import org.storm.physics.math.extensions.toVector
import org.storm.physics.math.geometry.LineSegment

open class Circle(
    override val center: Point,
    val radius: Double,
) : Shape {

    val diameter: Double = radius * 2

    override val edges: List<LineSegment> = emptyList()

    constructor(x: Double, y: Double, radius: Double) : this(Point(x, y), radius)

    override fun contains(p: Point): Boolean = this.center.getSquaredDistance(p) <= this.radius * this.radius

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(fill = true)) {
            it.drawEllipse(x + center.x - radius, y + center.y - radius, diameter, diameter)
        }
    }

    override fun translate(dx: Double, dy: Double) {
        this.center.translate(dx, dy)
    }

    override fun rotate(point: Point, angle: Double) {
        this.center.rotate(point, angle)
    }

    override fun project(axis: Vector): Interval {
        val projectionLength = this.center.toVector().dot(axis)
        return Interval(projectionLength - radius, projectionLength + radius)
    }

    override fun toString(): String = "Circle(center = $center, radius: $radius)"

}
