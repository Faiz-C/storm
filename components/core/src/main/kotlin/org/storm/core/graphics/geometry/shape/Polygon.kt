package org.storm.core.graphics.geometry.shape

import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Geometric
import org.storm.core.graphics.geometry.Point

/**
 * Represents a polygon with N vertices in 2D space. Vertices are supplied in clockwise order such that edges would be
 * (v_i, v_i+1).
 */
open class Polygon(
    vararg vertices: Point
): Geometric {

    // Order and uniqueness matters for vertices
    val vertices: List<Point> = run {
        val vertices = vertices.distinct()

        require(vertices.size > 2) { "a polygon must have a minimum of 3 distinct vertices" }

        val first = vertices.first()
        require(vertices.any { it.x != first.x } || vertices.any { it.y != first.y }) {
            "Not all vertices can fall on the same x or y coordinate"
        }

        vertices
    }

    override fun translate(dx: Double, dy: Double) {
        this.vertices.forEach { point -> point.translate(dx, dy) }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(fill = true)) {
            drawPolygon(this@Polygon.vertices)
        }
    }

    override fun toString(): String = "Polygon(vertices=$vertices)"

}
