package org.storm.physics

import org.storm.core.context.Context
import org.storm.core.extensions.units
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.physics.entity.Entity
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

    var entities: Set<Entity> = setOf()
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
        this.entities.forEach { entity: Entity ->
            entity.collisionState.clear()

            entity.boundaries.forEach { (_, boundary) ->
                this.collisionStructure.insert(entity, boundary)
            }
        }
    }

    /**
     * Handles collision checks for the given Entity and returns the MTV to allow for adjustments in case of collisions.
     * If no collisions occur, a zero vector should be returned.
     *
     * @param entity Entity to check collisions for
     */
    protected abstract fun processCollisions(entity: Entity)

    /**
     * Processes standard Physics for a given entity based on its physics data up to this point.
     *
     * @param entity Entity to process physics for
     * @param elapsedTime elapsed time (in seconds) since the physics of this entity was last processed
     */
    private fun processPhysics(entity: Entity, elapsedTime: Double) {
        // Apply all forces onto entity
        applyForces(entity, elapsedTime)

        // If the entity has zero or extremely little velocity then consider it at rest. This means we DON'T check collision
        val minimumRestVelocity = MINIMUM_REST_VELOCITY.units
        if (entity.velocity.squaredMagnitude < minimumRestVelocity * minimumRestVelocity) return

        // Check and process any collisions
        processCollisions(entity)

        // Translate the entity by the forces applied to it
        entity.translateByVelocity()
    }

    /**
     * Applies all forces to the given Entity.
     *
     * @param entity Entity to apply forces for
     * @param elapsedTime elapsed time (in seconds) since forces were last applied to this Entity
     */
    private fun applyForces(entity: Entity, elapsedTime: Double) {
        entity.actingForces.replaceAll { (x, y): Vector, remainingDuration: Double ->
            // Force = Mass * Acceleration. In 2D we have acceleration in both x and y directions.
            val aX = x / entity.mass // x acceleration
            val aY = y / entity.mass // y acceleration

            entity.velocity = entity.velocity.add(Vector(aX * elapsedTime, aY * elapsedTime))
            if (remainingDuration == INFINITE_DURATION) INFINITE_DURATION else remainingDuration - elapsedTime
        }

        entity.actingForces
            .entries
            .removeIf { (_, duration) -> duration != INFINITE_DURATION && duration <= 0 }
    }

}
