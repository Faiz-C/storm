package org.storm.core.graphics.geometry.shape

import org.storm.core.extensions.pixels
import org.storm.core.graphics.render.RenderContext
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

    override fun render(context: RenderContext, x: Double, y: Double) {
        context.withSettings(context.settings.copy(fill = true)) {
            drawEllipse(
                (x + center.x - width / 2).pixels,
                (y + center.y - height / 2).pixels,
                width.pixels,
                height.pixels
            )
        }
    }

    override fun translate(dx: Double, dy: Double) {
        this.center.translate(dx, dy)
    }
}