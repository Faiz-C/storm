package org.storm.impl.jfx.graphics

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.canvas.Settings
import org.storm.core.graphics.Image
import org.storm.core.graphics.geometry.Point
import kotlin.math.ceil

class JfxCanvas(private val gc: GraphicsContext): Canvas() {

    companion object {
        fun getTextBounds(text: String, font: org.storm.core.graphics.canvas.Font): Pair<Double, Double> {
            val textNode = Text(text)
            textNode.font = Font.font(
                font.type,
                FontWeight.findByWeight(font.weight),
                font.size
            )
            val bounds = textNode.layoutBounds
            return ceil(bounds.width) to ceil(bounds.height)
        }
    }

    override suspend fun clear() {
        gc.clearRect(0.0, 0.0, Context.RESOLUTION.width, Context.RESOLUTION.height)
    }

    override suspend fun onSettingsChange(settings: Settings) {
        val color = settings.color.toJfxColor()
        gc.fill = color
        gc.stroke = color
        gc.lineWidth = settings.thickness
        gc.font = Font.font(
            settings.font.type,
            FontWeight.findByWeight(settings.font.weight),
            settings.font.size
        )
    }

    override suspend fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        gc.strokeLine(x1, y1, x2, y2)
    }

    override suspend fun drawText(text: String, x: Double, y: Double) {
        val (_, textHeight) = getTextBounds(text, this.settings.font)
        if (this.settings.fill) {
            gc.fillText(text, x, y + textHeight)
        } else {
            gc.strokeText(text, x, y + textHeight)
        }
    }

    override suspend fun drawRect(x: Double, y: Double, width: Double, height: Double) {
        if (this.settings.fill) {
            gc.fillRect(x, y, width, height)
        } else {
            gc.strokeRect(x, y, width, height)
        }
    }

    override suspend fun drawEllipse(x: Double, y: Double, width: Double, height: Double) {
        if (this.settings.fill) {
            gc.fillOval(x, y, width, height)
        } else {
            gc.strokeOval(x, y, width, height)
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
            gc.fillPolygon(xCoordinates, yCoordinates, size)
        } else {
            gc.strokePolygon(xCoordinates, yCoordinates, size)
        }
    }

    override suspend fun drawImage(image: Image, x: Double, y: Double) {
        require(image is JfxImage) {
            "JfxCanvas only supports drawing JfxImage objects"
        }

        gc.drawImage(image.image, x, y)
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