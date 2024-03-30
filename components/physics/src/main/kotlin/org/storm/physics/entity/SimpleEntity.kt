package org.storm.physics.entity

import org.storm.physics.math.geometry.shapes.Shape

class SimpleEntity(boundaries: MutableMap<String, Shape>, speed: Double, mass: Double, restitution: Double) :
    Entity(boundaries, speed, mass, restitution) {

    constructor(
        boundary: Shape,
        speed: Double,
        mass: Double,
        restitution: Double
    ) : this(mutableMapOf(SINGLE_BOUNDARY to boundary), speed, mass, restitution)

}
