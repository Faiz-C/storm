package org.storm.core.graphics.geometry.shape

import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Geometric
import org.storm.core.graphics.geometry.Point

/**
 * A simple graphical implementation of an Ellipse
 */
open class Ellipse(
    val center: Point,
    val width: Double,
    val height: Double
): Geometric {

    constructor(x: Double, y: Double, width: Double, height: Double) : this(Point(x, y), width, height)

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(fill = true)) {
            it.drawEllipse(x + center.x - width / 2, y + center.y - height / 2, width, height)
        }
    }

    override fun translate(dx: Double, dy: Double) {
        this.center.translate(dx, dy)
    }
}