package org.storm.physics.math.geometry.shapes

import javafx.scene.canvas.GraphicsContext
import org.storm.core.context.Context
import org.storm.physics.context.UNIT_CONVERTOR
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.LineSegment
import org.storm.physics.math.geometry.Point

open class Circle(
    override val center: Point,
    val radius: Double,
) : Shape {

    val diameter: Double = radius * 2

    override val edges: List<LineSegment> = emptyList()

    constructor(x: Double, y: Double, radius: Double) : this(Point(x, y), radius)

    override fun contains(p: Point): Boolean = this.center.getSquaredDistance(p) <= this.radius * this.radius

    override suspend fun render(gc: GraphicsContext, x: Double, y: Double) {
        val unitConvertor = Context.UNIT_CONVERTOR

        gc.fillOval(
            x + unitConvertor.toPixels(this.center.x - this.radius),
            y + unitConvertor.toPixels(this.center.y - this.radius),
            unitConvertor.toPixels(this.diameter),
            unitConvertor.toPixels(this.diameter)
        )
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
