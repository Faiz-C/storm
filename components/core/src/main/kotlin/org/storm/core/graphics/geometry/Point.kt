package org.storm.core.graphics.geometry

import org.storm.core.extensions.pixels
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas

/**
 * Represents a point in 2D space. The x and y values of a Point are arbitrary *unit* values for
 * the 2D space. They may not be 1:1 with pixels on the screen.
 */
data class Point(
    var x: Double,
    var y: Double
) : Geometric {

    /**
     * @returns A version of this Point with x and y values converted to pixels
     */
    fun toPixels() = Point(x.pixels, y.pixels)

    override fun translate(dx: Double, dy: Double) {
        this.x += dx
        this.y += dy
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.drawEllipseWithUnits(x + this.x, y + this.y, 1.units, 1.units)
    }

    override fun toString(): String = "Point(x=$x, y=$y)"

}