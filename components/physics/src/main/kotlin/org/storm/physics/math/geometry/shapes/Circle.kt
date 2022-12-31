package org.storm.physics.math.geometry.shapes

import org.storm.core.render.Renderable
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.LineSegment
import org.storm.physics.math.geometry.Point
import org.storm.physics.transforms.UnitConvertor

open class Circle(
  override val center: Point,
  val radius: Double,
  val diameter: Double = radius * 2
) : Shape {

  override val edges: List<LineSegment> = emptyList()

  constructor(x: Double, y: Double, radius: Double) : this(Point(x, y), radius)

  override fun contains(p: Point): Boolean = this.center.getSquaredDistance(p) <= this.radius * this.radius

  override fun transform(unitConvertor: UnitConvertor) = Renderable { gc, x, y ->
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

  override fun project(axis: Vector): Interval {
    val projectionLength = this.center.toVector().dot(axis)
    return Interval(projectionLength - radius, projectionLength + radius)
  }

}
