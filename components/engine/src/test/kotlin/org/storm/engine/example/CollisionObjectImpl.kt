package org.storm.engine.example

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.collision.CollisionObject
import org.storm.physics.math.geometry.shapes.CollidableShape

class CollisionObjectImpl(
    hurtBox: CollidableShape,
    speed: Double,
    mass: Double,
    restitution: Double
) : CollisionObject(
    hurtBox,
    speed,
    mass,
    restitution
) {

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        super.render(canvas, x, y)
    }

}
