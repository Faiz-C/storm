package org.storm.engine.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import org.storm.core.render.Renderable
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.transforms.UnitConvertor

class ImmovableRectEntity(
    x: Double,
    y: Double,
    width: Double,
    height: Double
) : ImmovableEntity(AABB(x, y, width, height)) {

    override suspend fun render(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = Color.RED
        super.render(gc, x, y)
    }

}
