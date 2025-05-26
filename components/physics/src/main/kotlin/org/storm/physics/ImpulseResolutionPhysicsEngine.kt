package org.storm.physics

import org.apache.commons.math3.util.FastMath
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.physics.collision.CollisionDetector.checkMtv
import org.storm.physics.entity.PhysicsObject
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

    override fun processCollisions(physicsObject: PhysicsObject) {
        physicsObject.boundaries.forEach boundaries@{ (_, section) ->
            this.collisionStructure.getCloseNeighbours(physicsObject, section)
                .forEach neighbourCollisionCheck@{ (neighbourSection, neighbour) ->
                    // Ignore if already interacted with
                    // This is intentionally checking the neighbours collision state instead of this entity's since we update
                    // our collision state during these checks and not our neighbours
                    if (neighbour.collisionState[physicsObject]?.contains(section) == true) return@neighbourCollisionCheck

                    // Initially we can say that these two did not collide, but still track that we checked the two
                    if (!physicsObject.collisionState.containsKey(neighbour)) {
                        physicsObject.collisionState[neighbour] = setOf()
                    }

                    // Check collision and get back the minimum translation vector
                    val mtv = checkMtv(section, neighbourSection)

                    if (mtv === Vector.ZERO_VECTOR) return@neighbourCollisionCheck

                    // resolve collision via impulse resolution
                    impulsiveResolution(physicsObject, neighbour, mtv)

                    // Allow the entities to react to colliding with each other
                    physicsObject.react(neighbour)
                    neighbour.react(physicsObject)

                    // Update the collision state to say that we collided with this entity
                    physicsObject.collisionState[neighbour] = physicsObject.collisionState[neighbour]!!.plus(section)
                }
        }
    }

    /**
     * Resolves the collision between the two entities using impulse resolution.
     *
     * @param e1 Entity involved in the collision
     * @param e2 Entity involved in the collision
     * @param mtv minimum translation vector calculated from the collision
     */
    private fun impulsiveResolution(e1: PhysicsObject, e2: PhysicsObject, mtv: Vector) {
        val collisionNormal = mtv.normalized
        val relativeVelocity = e1.velocity.subtract(e2.velocity)
        val contactVelocity = relativeVelocity.dot(collisionNormal)

        // Ignore if the velocities are moving away
        if (contactVelocity > 0) return

        // Calculate Impulse vector
        val minRestitution = FastMath.min(e1.restitution, e2.restitution) // minimum restitution constant is used
        val impulse = -(1.0 + minRestitution) * contactVelocity / (e1.inverseMass + e2.inverseMass)
        val impulseVector = collisionNormal.scale(impulse)

        // Apply impulse vectors respectively
        e1.velocity = e1.velocity.add(impulseVector.scale(e1.inverseMass))
        e2.velocity = e2.velocity.subtract(impulseVector.scale(e2.inverseMass))

        // Positional correction do to eventual floating point arithmetic error
        val mag = FastMath.max(mtv.magnitude - POSITIONAL_CORRECTION_THRESHOLD.units, 0.0)
        val correction =
            mag / (e1.inverseMass + e2.inverseMass) * POSITIONAL_CORRECTION_ADJUSTMENT.units

        val requiredE1Translation = collisionNormal.scale(correction * e1.inverseMass)
        val requiredE2Translation = collisionNormal.scale(-correction * e2.inverseMass)

        e1.translate(requiredE1Translation.x, requiredE1Translation.y)
        e2.translate(requiredE2Translation.x, requiredE2Translation.y)
    }

}
