package org.storm.physics

import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.Renderable
import org.storm.core.update.Updatable
import org.storm.physics.collision.CollisionObject
import org.storm.physics.math.Vector
import org.storm.physics.structures.SpatialDataStructure

/**
 * A PhysicsEngine is an abstract representation of a game engine's physics core. A PhysicsEngine deals with applying
 * forces to Entities within the game and dealing with standard collisions. Physics using this engine are done using
 * arbitrary units in the form of doubles, they may not be 1:1 with pixels.
 *
 */
abstract class PhysicsEngine protected constructor(
    protected val collisionStructure: SpatialDataStructure
) : Updatable, Renderable {

    companion object {
        private const val INFINITE_DURATION = Double.NEGATIVE_INFINITY
        private const val MINIMUM_REST_VELOCITY = 0.0001 // In pixels as the unit conversion is up to the User
    }

    var paused = false

    protected val entityLock: Any = Any()

    var entities: Set<CollisionObject> = setOf()
        set(value) {
            synchronized(this.entityLock) {
                field = value
                this.collisionStructure.clear()
                resetCollisionData()
            }
        }


    /**
     * Clears all forces from all entities being tracked
     */
    fun clearAllForces() {
        synchronized(this.entityLock) { this.entities.forEach { entity -> entity.actingForces.clear() } }
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        if (this.paused) return

        synchronized(this.entityLock) {
            resetCollisionData()
            this.entities.forEach { entity -> processPhysics(entity, elapsedTime) }
        }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        this.collisionStructure.render(canvas, 0.0, 0.0)
    }

    /**
     * Resets all collision data for all entities.
     */
    protected open fun resetCollisionData() {
        this.collisionStructure.clear()
        this.entities.forEach { collisionObject: CollisionObject ->
            collisionObject.collisionState.clear()

            collisionObject.boundaries.forEach { (_, boundary) ->
                this.collisionStructure.insert(collisionObject, boundary)
            }
        }
    }

    /**
     * Handles collision checks for the given Entity and returns the MTV to allow for adjustments in case of collisions.
     * If no collisions occur, a zero vector should be returned.
     *
     * @param collisionObject Entity to check collisions for
     */
    protected abstract fun processCollisions(collisionObject: CollisionObject)

    /**
     * Processes standard Physics for a given entity based on its physics data up to this point.
     *
     * @param collisionObject Entity to process physics for
     * @param elapsedTime elapsed time (in seconds) since the physics of this entity was last processed
     */
    private fun processPhysics(collisionObject: CollisionObject, elapsedTime: Double) {
        // Apply all forces onto entity
        applyForces(collisionObject, elapsedTime)

        // If the entity has zero or extremely little velocity then consider it at rest. This means we DON'T check collision
        val minimumRestVelocity = MINIMUM_REST_VELOCITY.units
        if (collisionObject.velocity.squaredMagnitude < minimumRestVelocity * minimumRestVelocity) return

        // Check and process any collisions
        processCollisions(collisionObject)

        // Translate the entity by the forces applied to it
        collisionObject.translateByVelocity()
    }

    /**
     * Applies all forces to the given Entity.
     *
     * @param collisionObject Entity to apply forces for
     * @param elapsedTime elapsed time (in seconds) since forces were last applied to this Entity
     */
    private fun applyForces(collisionObject: CollisionObject, elapsedTime: Double) {
        collisionObject.actingForces.replaceAll { (x, y): Vector, remainingDuration: Double ->
            // Force = Mass * Acceleration. In 2D we have acceleration in both x and y directions.
            val aX = x / collisionObject.mass // x acceleration
            val aY = y / collisionObject.mass // y acceleration

            collisionObject.velocity = collisionObject.velocity.add(Vector(aX * elapsedTime, aY * elapsedTime))
            if (remainingDuration == INFINITE_DURATION) INFINITE_DURATION else remainingDuration - elapsedTime
        }

        collisionObject.actingForces
            .entries
            .removeIf { (_, duration) -> duration != INFINITE_DURATION && duration <= 0 }
    }

}
