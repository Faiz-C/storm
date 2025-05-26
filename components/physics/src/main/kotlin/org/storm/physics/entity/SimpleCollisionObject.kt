package org.storm.physics.entity

import org.storm.physics.collision.CollisionObject
import org.storm.physics.math.geometry.shapes.CollidableShape

class SimpleCollisionObject(boundaries: MutableMap<String, CollidableShape>, speed: Double, mass: Double, restitution: Double) :
    CollisionObject(boundaries, speed, mass, restitution) {

    constructor(
        boundary: CollidableShape,
        speed: Double,
        mass: Double,
        restitution: Double
    ) : this(mutableMapOf(SINGLE_BOUNDARY to boundary), speed, mass, restitution)

}
