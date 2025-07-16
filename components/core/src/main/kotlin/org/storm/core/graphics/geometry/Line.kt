package org.storm.core.graphics.geometry

import org.storm.core.graphics.canvas.Canvas

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

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.drawLineWithUnits(x + this.start.x, y + this.start.y, this.end.x, this.end.y)
    }

    override fun toString(): String = "Line(start=$start, end=$end)"

}
