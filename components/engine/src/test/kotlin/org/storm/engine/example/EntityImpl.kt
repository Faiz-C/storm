package org.storm.engine.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import org.storm.core.render.Renderable
import org.storm.physics.entity.Entity
import org.storm.physics.math.geometry.shapes.Shape
import org.storm.physics.transforms.UnitConvertor

class EntityImpl(
    hurtBox: Shape,
    speed: Double,
    mass: Double,
    restitution: Double
) : Entity(
    hurtBox,
    speed,
    mass,
    restitution
) {

    override suspend fun render(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = Color.BLACK
        super.render(gc, x, y)
    }

}
