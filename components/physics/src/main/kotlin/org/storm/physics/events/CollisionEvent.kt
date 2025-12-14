package org.storm.physics.events

import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.CollidableShape

data class CollisionEvent(
    val c1: Collider,
    val c2: Collider,
    val c1Boundary: CollidableShape,
    val c2Boundary: CollidableShape
)
