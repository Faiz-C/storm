package org.storm.engine.example

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.entity.Entity
import org.storm.physics.math.geometry.shapes.Shape

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

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        super.render(canvas, x, y)
    }

}
