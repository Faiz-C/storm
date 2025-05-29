package org.storm.physics

import org.apache.commons.math3.util.FastMath
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.physics.collision.CollisionDetector.checkMtv
import org.storm.physics.collision.Collider
import org.storm.physics.math.Vector
import org.storm.physics.structures.QuadrantTree

/**
 * A ImpulseResolutionPhysicsEngine is straight forward and basic implementation of a PhysicsEngines.
 * It uses a Quad Tree (QuadrantTree) to check likely collisions and then uses impulse resolution to deal with collisions.
 */
class ImpulseResolutionPhysicsEngine : PhysicsEngine(
    QuadrantTree(0, Context.RESOLUTION_IN_UNITS.width, Context.RESOLUTION_IN_UNITS.height)
) {

    companion object {
        private const val POSITIONAL_CORRECTION_ADJUSTMENT = 0.2 // This is in pixels as the unit conversion is up to the user
        private const val POSITIONAL_CORRECTION_THRESHOLD = 0.01 // This is in pixels as the unit conversion is up to the user
    }

    // I think collision states should be held in the engine, not on the colliders
    // We are doubling up on memory by storing on both ends when we don't need to

    override suspend fun processCollisions(collider: Collider) {
        collider.boundaries.forEach boundaries@{ (_, section) ->
            this.collisionStructure.getCloseNeighbours(collider, section)
                .forEach neighbourCollisionCheck@{ (neighbourSection, neighbour) ->

                    // If the neighbour has already checked for collisions against this section of our collider
                    // then we can safely skip this check ourselves. No need to check twice
                    if (hasCheckedCollision(collider, neighbour, section)) return@neighbourCollisionCheck

                    // Check collision and get back the minimum translation vector
                    val mtv = checkMtv(section, neighbourSection)

                    if (mtv === Vector.ZERO_VECTOR) {
                        // Update the collision states and mark these as not collided
                        updateCollisionState(collider, neighbourSection, false)
                        return@neighbourCollisionCheck
                    }

                    // resolve collision via impulse resolution
                    impulsiveResolution(collider, neighbour, mtv)

                    // Update the collision states, mark these as collided, and trigger collision handlers
                    collider.collide(neighbour, neighbourSection)
                    neighbour.collide(collider, section)
                    updateCollisionState(collider, neighbourSection, true)
                }
        }
    }

    /**
     * Resolves the collision between the two colliders using impulse resolution.
     *
     * @param c1 Collider involved in the collision
     * @param c2 Collider involved in the collision
     * @param mtv minimum translation vector calculated from the collision
     */
    private fun impulsiveResolution(c1: Collider, c2: Collider, mtv: Vector) {
        val collisionNormal = mtv.normalized
        val relativeVelocity = c1.velocity.subtract(c2.velocity)
        val contactVelocity = relativeVelocity.dot(collisionNormal)

        // Ignore if the velocities are moving away
        if (contactVelocity > 0) return

        // Calculate Impulse vector
        val minRestitution = FastMath.min(c1.restitution, c2.restitution) // minimum restitution constant is used
        val impulse = -(1.0 + minRestitution) * contactVelocity / (c1.inverseMass + c2.inverseMass)
        val impulseVector = collisionNormal.scale(impulse)

        // Apply impulse vectors respectively
        c1.velocity = c1.velocity.add(impulseVector.scale(c1.inverseMass))
        c2.velocity = c2.velocity.subtract(impulseVector.scale(c2.inverseMass))

        // Positional correction do to eventual floating point arithmetic error
        val mag = FastMath.max(mtv.magnitude - POSITIONAL_CORRECTION_THRESHOLD.units, 0.0)
        val correction = mag / (c1.inverseMass + c2.inverseMass) * POSITIONAL_CORRECTION_ADJUSTMENT.units

        val requiredE1Translation = collisionNormal.scale(correction * c1.inverseMass)
        val requiredE2Translation = collisionNormal.scale(-correction * c2.inverseMass)

        c1.translate(requiredE1Translation.x, requiredE1Translation.y)
        c2.translate(requiredE2Translation.x, requiredE2Translation.y)
    }

}
