package org.storm.physics

import org.storm.core.event.EventManager
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.Renderable
import org.storm.physics.collision.Collider
import org.storm.physics.events.CollisionEvent
import org.storm.physics.events.getCollisionEventStream
import org.storm.physics.math.geometry.shapes.CollidableShape
import org.storm.physics.structures.SpatialDataStructure

/**
 * A PhysicsEngine is an abstract representation of a game engine's physics core. A PhysicsEngine deals with applying
 * forces to Entities within the game and dealing with standard collisions. Physics using this engine are done using
 * arbitrary units in the form of doubles, they may not be 1:1 with pixels.
 */
abstract class PhysicsEngine protected constructor(
    protected val collisionStructure: SpatialDataStructure
) : Renderable {

    companion object {
        private const val MINIMUM_REST_VELOCITY = 0.0001 // In pixels as the unit conversion is up to the User
    }

    var paused = false

    private val collisions: MutableSet<Pair<CollidableShape, CollidableShape>> = mutableSetOf()

    /**
     * Updates the physics simulation for all given colliders.
     *
     * This is the main entry point for the physics engine, called once per frame.
     * It prepares the collision detection system, then processes forces, collisions,
     * and movement for each collider.
     *
     * @param colliders The set of active colliders to simulate
     * @param elapsedTime The time elapsed since the last update in seconds
     */
    suspend fun update(colliders: Set<Collider>, elapsedTime: Double) {
        if (this.paused) return

        setColliders(colliders)
        colliders.forEach { collider -> processPhysics(collider, elapsedTime) }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        this.collisionStructure.render(canvas, 0.0, 0.0)
    }

    /**
     * Records the fact that we checked collisions between these two CollidableShapes (e.g., collider boundaries).
     * This is used to ensure we don't over check collisions when processing.
     *
     * @param b1 CollidableShape included in the check
     * @param b2 CollidableShape included in the check
     * @return true if this is a newly recorded check, false if we have already seen it
     */
    protected fun recordCollisionCheck(b1: CollidableShape, b2: CollidableShape): Boolean {
        val pair = if (b1.hashCode() > b2.hashCode()) {
            b1 to b2
        } else {
            b2 to b1
        }
        return this.collisions.add(pair)
    }

    /**
     * Creates a collision event if at least one of the two colliders has `eventOnCollision` set to true.
     *
     * @param c1 Collider involved in collision
     * @param c2 Collider involved in collision
     * @param b1 CollidableShape representing the boundary of c1 that was involved in the collision
     * @param b2 CollidableShape representing the boundary of c2 that was involved in the collision
     */
    protected fun createCollisionEvent(
        c1: Collider,
        c2: Collider,
        b1: CollidableShape,
        b2: CollidableShape
    ) {
        if (!c1.eventOnCollision && !c2.eventOnCollision) return

        EventManager.getCollisionEventStream()
            .produce(CollisionEvent(c1, c2, b1, b2))
    }

    /**
     * Handles collision checks for the given Collider and returns the MTV to allow for adjustments in case of collisions.
     * If no collisions occur, a zero vector should be returned.
     *
     * @param collider Collider to check collisions for
     */
    protected abstract suspend fun processCollisions(collider: Collider)

    /**
     * Prepares the physics engine for the next update cycle with the given colliders.
     * @param colliders The set of active colliders to process
     */
    private fun setColliders(colliders: Set<Collider>) {
        this.collisionStructure.clear()
        this.collisions.clear()

        colliders.forEach { collider: Collider ->
            collider.boundaries.forEach { (_, boundary) ->
                this.collisionStructure.insert(collider, boundary)
            }
        }
    }

    /**
     * Processes standard Physics for a given entity based on its physics data up to this point.
     *
     * @param collider Collider to process physics for
     * @param elapsedTime elapsed time (in seconds) since the physics of this entity was last processed
     */
    private suspend fun processPhysics(collider: Collider, elapsedTime: Double) {
        // Apply all forces onto entity
        collider.applyForces(elapsedTime)

        // If the entity has zero or extremely little velocity then consider it at rest. This means we DON'T check collision
        val minimumRestVelocity = MINIMUM_REST_VELOCITY.units
        if (collider.velocity.squaredMagnitude < minimumRestVelocity * minimumRestVelocity) return

        // Check and process any collisions
        processCollisions(collider)

        // Translate the entity
        collider.translateByVelocity()
    }
}
