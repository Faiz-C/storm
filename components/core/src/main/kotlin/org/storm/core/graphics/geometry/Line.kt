package org.storm.core.graphics.geometry

import org.storm.core.extensions.pixels
import org.storm.core.graphics.render.RenderContext

/**
 * Represents a finite line segment in 2D space.
 */
open class Line(
    start: Point,
    end: Point
): Geometric {

    val start = start.copy()
    val end = end.copy()

    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this(Point(x1, y1), Point(x2, y2))

    override fun translate(dx: Double, dy: Double) {
        this.start.translate(dx, dy)
        this.end.translate(dx, dy)
    }

    override fun render(context: RenderContext, x: Double, y: Double) {
        context.drawLine(
            (x + start.x).pixels,
            (y + start.y).pixels,
            end.x.pixels,
            end.y.pixels
        )
    }

    override fun toString(): String = "Line(start=$start, end=$end)"

}
