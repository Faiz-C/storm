package org.storm.physics.entity

import org.storm.physics.collision.CollisionObject
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * An ImmovableEntity is a normal Entity but cannot be moved.
 */
open class ImmovableCollisionObject(boundaries: MutableMap<String, CollidableShape>) : CollisionObject(boundaries, 0.0, Double.POSITIVE_INFINITY, 1.0) {

    constructor(boundary: CollidableShape) : this(mutableMapOf(SINGLE_BOUNDARY to boundary))

    override fun translate(dx: Double, dy: Double) {
        // ImmovableEntity does not move
    }

}
