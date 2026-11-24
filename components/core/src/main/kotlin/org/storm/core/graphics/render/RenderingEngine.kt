package org.storm.core.graphics.render

/**
 * A RenderingEngine handles the full rendering process end-to-end. Due to the potential async nature of the
 * implementation, it is responsible for tracking render timings using
 * [org.storm.core.telemetry.TelemetryTracker.recordRender].
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
     * Renders by calling [block] and uses the given [alpha] value to interpolate if needed.
     *
     * @param alpha a value between [0.0, 1.0] which represents how far along the game logic is to the next complete cycle.
     * @param block a block of code to execute on the [RenderContext]
     */
    fun render(alpha: Double, block: RenderContext.() -> Unit)

}