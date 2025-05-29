package org.storm.core.graphics

import org.storm.core.graphics.canvas.Canvas

/**
 * A Renderable is an object which understands how to represent itself on a canvas.
 */
fun interface Renderable {

    /**
     * Renders the object onto the given Canvas.
     *
     * @param canvas Canvas to render onto
     * @param x base x position to render from
     * @param y base y position to render from
     */
    suspend fun render(canvas: Canvas, x: Double, y: Double)

}
