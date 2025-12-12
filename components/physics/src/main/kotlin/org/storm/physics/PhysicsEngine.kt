package org.storm.physics

import kotlinx.coroutines.sync.withLock
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.Renderable
import org.storm.physics.collision.Collider
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.CollidableShape
import org.storm.physics.structures.SpatialDataStructure
import java.util.concurrent.ConcurrentHashMap

/**
 * A PhysicsEngine is an abstract representation of a game engine's physics core. A PhysicsEngine deals with applying
 * forces to Entities within the game and dealing with standard collisions. Physics using this engine are done using
 * arbitrary units in the form of doubles, they may not be 1:1 with pixels.
 */
abstract class PhysicsEngine protected constructor(
    protected val collisionStructure: SpatialDataStructure
) : Renderable {

    companion object {
        private const val INFINITE_DURATION = Double.NEGATIVE_INFINITY
        private const val MINIMUM_REST_VELOCITY = 0.0001 // In pixels as the unit conversion is up to the User
    }

    var paused = false

    // Tracks what forces exist in the game, what colliders they are acting on, and for how long
    private val forces: MutableMap<Vector, MutableMap<Collider, Double>> = mutableMapOf()
    private val collisions: MutableMap<Collider, MutableMap<CollidableShape, Boolean>> = mutableMapOf()

    /**
     * Applies the force to the collider for the given duration (in seconds).
     *
     * @param force Vector representing the force to apply
     * @param collider Collider to apply the force to
     * @param duration How long to apply the force for in seconds
     */
    fun applyForce(force: Vector, collider: Collider, duration: Double = INFINITE_DURATION) {
        this.forces.computeIfPresent(force) { _, actingColliders ->
            actingColliders.computeIfPresent(collider) { _, remainingDuration ->
                remainingDuration + duration
            }
            actingColliders.putIfAbsent(collider, duration)
            actingColliders
        }

        this.forces.putIfAbsent(
            force,
            ConcurrentHashMap<Collider, Double>().apply {
                this[collider] = duration
            }
        )
    }

    /**
     * Clears all forces from all entities being tracked
     */
    fun clearAllForces() {
        this.forces.clear()
    }

    suspend fun update(colliders: Set<Collider>, elapsedTime: Double) {
        if (this.paused) return

        setColliders(colliders)
        colliders.forEach { collider -> processPhysics(collider, elapsedTime) }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        this.collisionStructure.render(canvas, 0.0, 0.0)
    }

    /**
     * Sets the collision related data with
     */
    protected open suspend fun setColliders(colliders: Set<Collider>) {
        // Clear the spatial data structure and collisions
        this.collisionStructure.clear()
        this.collisions.clear()

        // For any colliders that don't exist in the given set we need to stop applying force to them
        this.forces.forEach { (vector, colliders) ->
            colliders.keys.filter {
                it !in colliders
            }.forEach {
                colliders.remove(it)
            }
        }

        colliders.forEach { collider: Collider ->
            collider.boundaries.forEach { (_, boundary) ->
                this.collisionStructure.insert(collider, boundary)
            }
        }
    }

    /**
     * Checks if two colliders have collided on a specific boundary
     *
     * @param c1 Collider to check
     * @param c2 Collider to check
     * @param boundary CollidableShape representing the boundary to check
     * @return true if a collision has occurred, false otherwise
     */
    protected fun hasCheckedCollision(c1: Collider, c2: Collider, boundary: CollidableShape): Boolean {
        return collisions[c1]?.contains(boundary) == true || collisions[c2]?.contains(boundary) == true
    }

    /**
     * Updates the collision state of a collider with respect to the boundary.
     *
     * @param collider Collider whose state to update
     * @param boundary CollidableShape representing the boundary to update
     * @param collided true if a collision occurred, false otherwise
     */
    protected fun updateCollisionState(collider: Collider, boundary: CollidableShape, collided: Boolean) {
        collisions.putIfAbsent(collider, mutableMapOf())
        collisions[collider]!![boundary] = collided
    }

    /**
     * Handles collision checks for the given Collider and returns the MTV to allow for adjustments in case of collisions.
     * If no collisions occur, a zero vector should be returned.
     *
     * @param collider Collider to check collisions for
     */
    protected abstract suspend fun processCollisions(collider: Collider)

    /**
     * Processes standard Physics for a given entity based on its physics data up to this point.
     *
     * @param collider Collider to process physics for
     * @param elapsedTime elapsed time (in seconds) since the physics of this entity was last processed
     */
    private suspend fun processPhysics(collider: Collider, elapsedTime: Double) {
        // Apply all forces onto entity
        applyForces(collider, elapsedTime)

        // If the entity has zero or extremely little velocity then consider it at rest. This means we DON'T check collision
        val minimumRestVelocity = MINIMUM_REST_VELOCITY.units
        if (collider.velocity.squaredMagnitude < minimumRestVelocity * minimumRestVelocity) return

        // Check and process any collisions
        processCollisions(collider)

        // Translate the entity by the forces applied to it
        collider.translateByVelocity()
    }

    /**
     * Applies all forces acting on the given collider.
     *
     * @param collider Collider to apply forces for
     * @param elapsedTime elapsed time (in seconds) since forces were last applied to this Collider
     */
    private fun applyForces(collider: Collider, elapsedTime: Double) {
        this.forces.forEach { (x, y), actingColliders ->
            actingColliders[collider]?.let { remainingDuration ->
                val aX = x / collider.mass // x acceleration
                val aY = y / collider.mass // y acceleration

                collider.velocity = collider.velocity.add(Vector(aX * elapsedTime, aY * elapsedTime))
                val adjustedDuration = if (remainingDuration == INFINITE_DURATION) INFINITE_DURATION else remainingDuration - elapsedTime

                if (adjustedDuration != INFINITE_DURATION && adjustedDuration <= 0) {
                    actingColliders.remove(collider)
                } else {
                    actingColliders[collider] = adjustedDuration
                }
            }
        }
    }
}
