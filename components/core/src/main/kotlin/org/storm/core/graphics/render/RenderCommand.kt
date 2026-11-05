package org.storm.core.graphics.render

import org.storm.core.graphics.canvas.Settings
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import org.storm.core.graphics.Image as FileImage

sealed interface RenderCommand {
    // State Management
    data class ChangeSettings(val settings: Settings): RenderCommand
    data class Clip(val boundary: Rectangle): RenderCommand
    object Unclip: RenderCommand

    // Primitives
    data class Line(val x1: Double, val y1: Double, val x2: Double, val y2: Double): RenderCommand
    data class Text(val text: String, val x: Double, val y: Double): RenderCommand
    data class Rect(val x: Double, val y: Double, val width: Double, val height: Double): RenderCommand
    data class Ellipse(val x: Double, val y: Double, val width: Double, val height: Double): RenderCommand
    data class Polygon(val points: List<Point>): RenderCommand
    data class Image(val image: FileImage, val x: Double, val y: Double): RenderCommand
}