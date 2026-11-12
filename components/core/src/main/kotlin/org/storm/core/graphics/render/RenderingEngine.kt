package org.storm.core.graphics.render

/**
 * A RenderingEngine handles the full rendering process end-to-end.
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
     * Sets the frames per second that the engine should render at.
     */
    fun setFps(fps: Int)

    /**
     * Renders the [block]
     *
     * @param block a block of code to execute on the [RenderContext]
     */
    fun render(block: RenderContext.() -> Unit)

}