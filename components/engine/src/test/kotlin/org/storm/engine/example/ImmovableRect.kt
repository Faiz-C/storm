package org.storm.engine.example

import org.storm.core.graphics.render.Renderable
import org.storm.core.graphics.render.RenderContext
import org.storm.core.graphics.render.brush.Color
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.AABB

class ImmovableRect(
    x: Double,
    y: Double,
    width: Double,
    height: Double
): Renderable {
    val collider = Collider(AABB(x, y, width, height), Double.POSITIVE_INFINITY, 1.0)

    override suspend fun render(context: RenderContext, x: Double, y: Double) {
        context.withSettings(context.settings.copy(color = Color(255.0, 0.0, 0.0, 1.0))) {
            collider.render(context, x, y)
        }
    }

}
