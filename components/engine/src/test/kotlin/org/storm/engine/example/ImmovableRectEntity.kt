package org.storm.engine.example

import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Color
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.math.geometry.shapes.AABB

class ImmovableRectEntity(
    x: Double,
    y: Double,
    width: Double,
    height: Double
) : ImmovableEntity(AABB(x, y, width, height)) {

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(color = Color(255.0, 0.0, 0.0, 1.0))) {
            super.render(canvas, x, y)
        }
    }

}
