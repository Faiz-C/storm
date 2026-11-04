package org.storm.core.graphics.canvas

import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import org.storm.core.graphics.Image as FileImage

sealed interface DrawCommand {
    data class Line(val x1: Double, val y1: Double, val x2: Double, val y2: Double): DrawCommand
    data class Text(val text: String, val x: Double, val y: Double): DrawCommand
    data class Rect(val x: Double, val y: Double, val width: Double, val height: Double): DrawCommand
    data class Ellipse(val x: Double, val y: Double, val width: Double, val height: Double): DrawCommand
    data class Polygon(val points: List<Point>): DrawCommand
    data class Image(val image: FileImage, val x: Double, val y: Double): DrawCommand
}