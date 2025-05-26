package org.storm.physics.collision

/**
 * An Impulsive Object is one which reacts when being collided with
 */
interface Impulsive {

    /**
     * Executes a reaction based on the Entity.
     *
     * @param collisionObject the Entity which collided with this one
     */
    fun react(collisionObject: CollisionObject)

}
