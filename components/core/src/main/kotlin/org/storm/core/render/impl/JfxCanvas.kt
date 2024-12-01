package org.storm.core.render.impl

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Settings
import org.storm.core.render.Image
import org.storm.core.render.geometry.PixelPoint

class JfxCanvas(private val gc: GraphicsContext): Canvas() {

    override suspend fun clear() {
        gc.clearRect(0.0, 0.0, Context.RESOLUTION.width, Context.RESOLUTION.height)
    }

    override suspend fun onSettingsChange(settings: Settings) {
        val color = Color(settings.color.red, settings.color.green, settings.color.blue, settings.color.alpha)
        gc.fill = color
        gc.stroke = color
        gc.lineWidth = settings.thickness
        gc.font = javafx.scene.text.Font.font(
            settings.font.type,
            FontWeight.findByWeight(settings.font.weight),
            settings.font.size
        )
    }

    override suspend fun drawTextWithPixels(text: String, x: Double, y: Double) {
        if (this.settings.fill) {
            gc.fillText(text, x, y)
        } else {
            gc.strokeText(text, x, y)
        }
    }

    override suspend fun drawRectWithPixels(x: Double, y: Double, width: Double, height: Double) {
        if (this.settings.fill) {
            gc.fillRect(x, y, width, height)
        } else {
            gc.strokeRect(x, y, width, height)
        }
    }

    override suspend fun drawEllipseWithPixels(x: Double, y: Double, width: Double, height: Double) {
        if (this.settings.fill) {
            gc.fillOval(x, y, width, height)
        } else {
            gc.strokeOval(x, y, width, height)
        }
    }

    override suspend fun drawPolygonWithPixels(points: List<PixelPoint>) {
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

    override suspend fun drawImageWithPixels(image: Image, x: Double, y: Double) {
        require(image is JfxImage) {
            "JfxCanvas only supports drawing JfxImage objects"
        }

        gc.drawImage(image.image, x, y)
    }
}