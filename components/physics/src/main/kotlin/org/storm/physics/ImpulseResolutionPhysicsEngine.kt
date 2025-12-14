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

    override suspend fun processCollisions(collider: Collider) {
        collider.boundaries.forEach boundaries@{ (_, section) ->
            this.collisionStructure.getCloseNeighbours(collider, section)
                .forEach neighbourCollisionCheck@{ (neighbourSection, neighbour) ->
                    // Record that we have checked these sections. If they have already been recorded then
                    // skip the logic below since it's already been done.
                    if (!recordCollisionCheck(section, neighbourSection)) return@neighbourCollisionCheck

                    // Check collision and get back the minimum translation vector
                    val mtv = checkMtv(section, neighbourSection)

                    if (mtv === Vector.ZERO_VECTOR) {
                        return@neighbourCollisionCheck
                    }

                    // Resolve collision via impulse resolution
                    impulsiveResolution(collider, neighbour, mtv)

                    // Create an event to let listeners act on the collision as needed
                    createCollisionEvent(
                        collider,
                        neighbour,
                        section,
                        neighbourSection
                    )
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

        val requiredC1Translation = collisionNormal.scale(correction * c1.inverseMass)
        val requiredC2Translation = collisionNormal.scale(-correction * c2.inverseMass)

        c1.translate(requiredC1Translation.x, requiredC1Translation.y)
        c2.translate(requiredC2Translation.x, requiredC2Translation.y)
    }

}
