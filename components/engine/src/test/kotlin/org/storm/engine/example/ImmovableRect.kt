package org.storm.engine.example

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.canvas.Color
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.AABB

class ImmovableRect(
    x: Double,
    y: Double,
    width: Double,
    height: Double
): Renderable {
    val collider = Collider(AABB(x, y, width, height), Double.POSITIVE_INFINITY, 1.0)

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(color = Color(255.0, 0.0, 0.0, 1.0))) {
            collider.render(canvas, x, y)
        }
    }

}
