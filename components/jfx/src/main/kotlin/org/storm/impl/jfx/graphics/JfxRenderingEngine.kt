package org.storm.impl.jfx.graphics

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.scene.text.TextBoundsType
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.graphics.Image
import org.storm.core.graphics.render.RenderContext
import org.storm.core.graphics.render.brush.FontMetrics
import org.storm.core.graphics.render.Settings
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import org.storm.core.graphics.render.RenderingEngine
import org.storm.core.graphics.render.buffered.BufferedRenderContext
import org.storm.core.graphics.render.buffered.RenderBufferExchange
import org.storm.core.graphics.render.buffered.RenderCommand
import kotlin.math.abs

class JfxRenderingEngine(
    bufferPoolSize: Int = 4,
    bufferSize: Int = 256,
    private val gc: GraphicsContext
) : RenderingEngine {

    companion object {
        private const val DEFAULT_FPS = 60

        fun getFontMetrics(text: String, font: org.storm.core.graphics.render.brush.Font): FontMetrics {
            val textNode = Text(text)
            textNode.font = Font.font(
                font.type,
                FontWeight.findByWeight(font.weight),
                font.size
            )

            // Logical Bounds (includes descent, ascent, and line spacing values)
            // This will give us the logical width and height to work with
            val logicalBounds = textNode.layoutBounds

            // Visual Bounds (excludes ascent and line spacing but includes descent as needed, think "g")
            textNode.boundsType = TextBoundsType.VISUAL
            val visualBounds = textNode.layoutBounds

            val ascent = abs(visualBounds.minY)
            val descent = visualBounds.height - ascent

            return FontMetrics(
                width = logicalBounds.width,
                height = logicalBounds.height,
                visualWidth = visualBounds.width,
                visualHeight = visualBounds.height,
                visualMinX = visualBounds.minX,
                visualMinY = visualBounds.minY,
                ascent = ascent,
                descent = descent
            )
        }
    }

    private val bufferExchange = RenderBufferExchange(bufferPoolSize, bufferSize)
    private val context = BufferedRenderContext()

    private var animationTimer = FrameLimitedAnimationTimer(DEFAULT_FPS, this::processBuffer)

    init {
        this.gc.textBaseline = VPos.TOP
        this.gc.textAlign = TextAlignment.LEFT
    }

    override fun start() {
        this.animationTimer.start()
    }

    override fun stop() {
        this.animationTimer.stop()
    }

    override fun setFps(fps: Int) {
        this.stop()
        this.animationTimer = FrameLimitedAnimationTimer(fps, this::processBuffer)
        this.start()
    }

    override fun render(block: RenderContext.() -> Unit) {
        // acquire a buffer for writing
        val buffer = this.bufferExchange.writeBuffer()

        // Use that record to that buffer
        this.context.withBuffer(buffer, block)

        // Submit the buffer to render next
        this.bufferExchange.submit(buffer)
    }

    private fun processBuffer() {
        val buffer = this.bufferExchange.renderingBuffer() ?: return

        this.execute(RenderCommand.Clear)

        for (command in buffer.commands) {
            this.execute(command)
        }
    }

    private fun execute(command: RenderCommand) {
        when (command) {
            // State Changes
            is RenderCommand.ChangeSettings -> this.onSettingsChange(command.settings)
            RenderCommand.Save -> this.gc.save()
            RenderCommand.Restore -> this.gc.restore()
            RenderCommand.Clear -> this.gc.clearRect(0.0, 0.0, Context.RESOLUTION.width, Context.RESOLUTION.height)

            // Actions
            is RenderCommand.Line -> this.gc.strokeLine(command.x1, command.y1, command.x2, command.y2)
            is RenderCommand.Text -> {
                if (this.context.settings.fill) {
                    this.gc.fillText(command.text, command.x, command.y)
                } else {
                    this.gc.strokeText(command.text, command.x, command.y)
                }
            }
            is RenderCommand.Rect -> {
                if (this.context.settings.fill) {
                    this.gc.fillRect(command.x, command.y, command.width, command.height)
                } else {
                    this.gc.strokeRect(command.x, command.y, command.width, command.height)
                }
            }
            is RenderCommand.Clip -> {
                val boundary = command.boundary
                this.gc.beginPath()
                this.gc.rect(boundary.x, boundary.y, boundary.width, boundary.height)
                this.gc.clip()
            }
            is RenderCommand.Ellipse -> {
                if (this.context.settings.fill) {
                    this.gc.fillOval(command.x, command.y, command.width, command.height)
                } else {
                    this.gc.strokeOval(command.x, command.y, command.width, command.height)
                }
            }
            is RenderCommand.Polygon -> drawPolygon(command.points)
            is RenderCommand.Image -> drawImage(command.image, command.x, command.y)
        }
    }

    private fun onSettingsChange(settings: Settings) {
        require(settings.alpha in 0.0..1.0) {
            "global alpha value must be within [0.0, 1.0]"
        }

        val color = settings.color.toJfxColor()
        this.gc.fill = color
        this.gc.stroke = color
        this.gc.lineWidth = settings.thickness
        this.gc.font = Font.font(
            settings.font.type,
            FontWeight.findByWeight(settings.font.weight),
            settings.font.size
        )
        this.gc.globalAlpha = settings.alpha
    }

    private fun drawPolygon(points: List<Point>) {
        val size = points.size
        val xCoordinates = DoubleArray(size)
        val yCoordinates = DoubleArray(size)

        for (i in 0 until size) {
            val (x1, y1) = points[i]
            xCoordinates[i] = x1
            yCoordinates[i] = y1
        }

        if (this.context.settings.fill) {
            this.gc.fillPolygon(xCoordinates, yCoordinates, size)
        } else {
            this.gc.strokePolygon(xCoordinates, yCoordinates, size)
        }
    }

    private fun drawImage(image: Image, x: Double, y: Double) {
        require(image is JfxImage) {
            "JfxCanvas only supports drawing JfxImage objects"
        }

        this.gc.drawImage(image.image, x, y)
    }

    private fun org.storm.core.graphics.render.brush.Color.toJfxColor(): Color {
        return Color(
            this.red / 255.0,
            this.green / 255.0,
            this.blue / 255.0,
            this.alpha
        )
    }

}