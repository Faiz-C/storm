package org.storm.physics.collision

import org.storm.physics.entity.PhysicsObject

/**
 * An Impulsive Object is one which reacts when being collided with
 */
interface Impulsive {

    /**
     * Executes a reaction based on the Entity.
     *
     * @param physicsObject the Entity which collided with this one
     */
    fun react(physicsObject: PhysicsObject)

}
