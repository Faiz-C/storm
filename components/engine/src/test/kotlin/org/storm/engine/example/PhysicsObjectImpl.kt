package org.storm.engine.example

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.entity.PhysicsObject
import org.storm.physics.math.geometry.shapes.CollidableShape

class PhysicsObjectImpl(
    hurtBox: CollidableShape,
    speed: Double,
    mass: Double,
    restitution: Double
) : PhysicsObject(
    hurtBox,
    speed,
    mass,
    restitution
) {

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        super.render(canvas, x, y)
    }

}
