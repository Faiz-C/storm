package org.storm.core.graphics.render

import org.storm.core.graphics.canvas.Canvas

/**
 * A RenderingPipeline abstracts the full end-to-end process of rendering objects onto the screen.
 */
interface RenderingEngine {

    /**
     * Starts the engine.
     */
    fun start()

    /**
     * Stops the engine.
     */
    fun stop()

    /**
     * Renders the [block]
     *
     * @param block a block of code to execute on the [Canvas]
     */
    fun render(block: Canvas.() -> Unit)

}