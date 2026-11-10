package org.storm.core.graphics.render.buffered

import org.storm.core.graphics.Image
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import org.storm.core.graphics.render.RenderContext
import org.storm.core.graphics.render.Settings

/**
 * An implementation of [RenderContext] which internally buffers commands to allow for async rendering.
 */
class BufferedRenderContext: RenderContext() {

    private var buffer: RenderBuffer? = null

    /**
     * Executes a [block] of rendering code with the given [buffer] set. Always attempts to unset the buffer.
     *
     * @param buffer the buffer to write commands to
     * @param block a block of code to execute with on the [buffer]
     */
    fun withBuffer(buffer: RenderBuffer, block: RenderContext.() -> Unit) {
        // This is important. If we somehow fail to unset the buffer from below something went very
        // wrong, and we should not continue.
        if (this.buffer != null) {
            throw IllegalStateException("BufferedRenderContext already has a buffer set.")
        }

        this.buffer = buffer

        try {
            block()
        } finally {
            this.buffer = null
        }
    }

    override fun onSettingsChange(settings: Settings) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.ChangeSettings(settings))
    }

    override fun withClip(boundary: Rectangle, block: RenderContext.() -> Unit) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Clip(boundary))
        block()
        this.buffer!!.add(RenderCommand.Unclip)
    }

    override fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Line(x1, y1, x2, y2))
    }

    override fun drawText(text: String, x: Double, y: Double) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Text(text, x, y))
    }

    override fun drawRect(x: Double, y: Double, width: Double, height: Double) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Rect(x, y, width, height))
    }

    override fun drawEllipse(x: Double, y: Double, width: Double, height: Double) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Ellipse(x, y, width, height))
    }

    override fun drawPolygon(points: List<Point>) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Polygon(points))
    }

    override fun drawImage(image: Image, x: Double, y: Double) {
        requireBuffer()
        this.buffer!!.add(RenderCommand.Image(image, x, y))
    }

    private fun requireBuffer() {
        require(this.buffer != null) {
            "Cannot execute drawing without a buffer set. Use `withBuffer` and supply a RenderingBuffer."
        }
    }

}