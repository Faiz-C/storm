package org.storm.core.graphics.render

/**
 * A Renderable is an object which understands how to represent itself on a canvas.
 */
fun interface Renderable {

    /**
     * Renders the object for a given frame.
     *
     * @param context RenderContext for the current frame
     * @param x base x position to render from
     * @param y base y position to render from
     */
    fun render(context: RenderContext, x: Double, y: Double)

}
