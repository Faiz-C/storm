package org.storm.physics.events

import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * Represents a collision event between Colliders c1 and c2 on boundaries b1 and b2 respectively.
 */
data class CollisionEvent(
    val c1: Collider,
    val c2: Collider,
    val b1: CollidableShape,
    val b2: CollidableShape
)
