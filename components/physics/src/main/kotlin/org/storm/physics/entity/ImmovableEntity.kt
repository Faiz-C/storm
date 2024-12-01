package org.storm.physics.entity

import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.Shape

/**
 * An ImmovableEntity is a normal Entity but cannot be moved.
 */
open class ImmovableEntity(boundaries: MutableMap<String, Shape>) : Entity(boundaries, 0.0, Double.POSITIVE_INFINITY, 1.0) {

    constructor(boundary: Shape) : this(mutableMapOf(SINGLE_BOUNDARY to boundary))

    override fun translate(dx: Double, dy: Double) {
        // ImmovableEntity does not move
    }

}
