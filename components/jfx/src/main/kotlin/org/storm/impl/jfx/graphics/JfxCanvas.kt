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
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.canvas.FontMetrics
import org.storm.core.graphics.canvas.Settings
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import kotlin.math.abs

class JfxCanvas(private val gc: GraphicsContext): Canvas() {

    companion object {
        fun getFontMetrics(text: String, font: org.storm.core.graphics.canvas.Font): FontMetrics {
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

    init {
        this.gc.textBaseline = VPos.TOP
        this.gc.textAlign = TextAlignment.LEFT
    }

    override suspend fun clear() {
        this.gc.clearRect(0.0, 0.0, Context.RESOLUTION.width, Context.RESOLUTION.height)
    }

    override suspend fun withClip(
        boundary: Rectangle,
        block: suspend Canvas.() -> Unit
    ) {
        this.gc.save()
        this.gc.beginPath()
        this.gc.rect(boundary.x, boundary.y, boundary.width, boundary.height)
        this.gc.clip()

        block()

        this.gc.restore()
    }

    override suspend fun onSettingsChange(settings: Settings) {
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

    override suspend fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        this.gc.strokeLine(x1, y1, x2, y2)
    }

    override suspend fun drawText(text: String, x: Double, y: Double) {
        if (this.settings.fill) {
            this.gc.fillText(text, x, y)
        } else {
            this.gc.strokeText(text, x, y)
        }
    }

    override suspend fun drawRect(x: Double, y: Double, width: Double, height: Double) {
        if (this.settings.fill) {
            this.gc.fillRect(x, y, width, height)
        } else {
            this.gc.strokeRect(x, y, width, height)
        }
    }

    override suspend fun drawEllipse(x: Double, y: Double, width: Double, height: Double) {
        if (this.settings.fill) {
            this.gc.fillOval(x, y, width, height)
        } else {
            this.gc.strokeOval(x, y, width, height)
        }
    }

    override suspend fun drawPolygon(points: List<Point>) {
        val size = points.size
        val xCoordinates = DoubleArray(size)
        val yCoordinates = DoubleArray(size)

        for (i in 0 until size) {
            val (x1, y1) = points[i]
            xCoordinates[i] = x1
            yCoordinates[i] = y1
        }

        if (this.settings.fill) {
            this.gc.fillPolygon(xCoordinates, yCoordinates, size)
        } else {
            this.gc.strokePolygon(xCoordinates, yCoordinates, size)
        }
    }

    override suspend fun drawImage(image: Image, x: Double, y: Double) {
        require(image is JfxImage) {
            "JfxCanvas only supports drawing JfxImage objects"
        }

        this.gc.drawImage(image.image, x, y)
    }

    private fun org.storm.core.graphics.canvas.Color.toJfxColor(): Color {
        return Color(
            this.red / 255.0,
            this.green / 255.0,
            this.blue / 255.0,
            this.alpha
        )
    }
}