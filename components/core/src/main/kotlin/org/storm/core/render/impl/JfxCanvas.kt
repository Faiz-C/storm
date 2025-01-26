package org.storm.core.render.impl

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Settings
import org.storm.core.render.Image
import org.storm.core.render.geometry.Point

class JfxCanvas(private val gc: GraphicsContext): Canvas() {

    override suspend fun clear() {
        gc.clearRect(0.0, 0.0, Context.RESOLUTION.width, Context.RESOLUTION.height)
    }

    override suspend fun onSettingsChange(settings: Settings) {
        val color = settings.color.toJfxColor()
        gc.fill = color
        gc.stroke = color
        gc.lineWidth = settings.thickness
        gc.font = javafx.scene.text.Font.font(
            settings.font.type,
            FontWeight.findByWeight(settings.font.weight),
            settings.font.size
        )
    }

    override suspend fun drawLineWithPixels(x1: Double, y1: Double, x2: Double, y2: Double) {
        gc.strokeLine(x1, y1, x2, y2)
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
        println("here")
        if (this.settings.fill) {
            gc.fillOval(x, y, width, height)
        } else {
            gc.strokeOval(x, y, width, height)
        }
    }

    override suspend fun drawPolygonWithPixels(points: List<Point>) {
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

    private fun org.storm.core.render.canvas.Color.toJfxColor(): Color {
        return Color(
            this.red / 255.0,
            this.blue / 255.0,
            this.green / 255.0,
            this.alpha
        )
    }
}