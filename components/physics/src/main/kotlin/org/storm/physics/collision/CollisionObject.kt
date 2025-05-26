package org.storm.physics.collision

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Geometric
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * Encapsulates collision and physics data for a game object
 */
class CollisionObject(
    val boundaries: MutableMap<String, CollidableShape>,
    mass: Double,
    val restitution: Double
) : Impulsive, Geometric, Renderable {

    companion object {
        private const val SINGLE_BOUNDARY = "single boundary"
        private const val INFINITE_DURATION = Double.NEGATIVE_INFINITY
    }

    val actingForces: MutableMap<Vector, Double> = mutableMapOf()
    val collisionState: MutableMap<CollisionObject, Set<CollidableShape>> = mutableMapOf()

    var velocity: Vector = Vector.Companion.ZERO_VECTOR

    var mass: Double = 1.0
        set(value) {
            require(value > 0) { "mass must be > 0" }
            inverseMass = 1.0 / value
            field = value
        }

    var inverseMass = 1.0
        private set

    val boundary: CollidableShape? get() = this.boundaries[SINGLE_BOUNDARY]

    init {
        require(!(this.restitution > 1 || this.restitution < 0)) { "restitution must be in the range [0, 1]" }
        this.mass = mass
    }

    constructor(
        boundary: CollidableShape,
        mass: Double,
        restitution: Double
    ) : this(mutableMapOf(SINGLE_BOUNDARY to boundary), mass, restitution)

    /**
     * Translates the Entity's position by its current velocity
     */
    fun translateByVelocity() {
        this.translate(velocity.x, velocity.y)
    }

    /**
     * Adds the given force (in Vector form) to be applied for given duration. Applying the same force
     * more than once will result in increasing the duration in which the force will be applied for.
     *
     * @param force force to apply (as a Vector)
     * @param duration how long (in seconds) to apply the force for
     */
    fun addForce(force: Vector, duration: Double = INFINITE_DURATION) {
        this.actingForces.computeIfPresent(force) { _, remainingDuration -> remainingDuration + duration }
        this.actingForces.putIfAbsent(force, duration)
    }

    /**
     * Removes the force (in Vector form) from the given Entity. i.e. stops applying the force permanently.
     *
     * @param force force to remove/stop applying (as a Vector)
     */
    fun removeForce(force: Vector) {
        this.actingForces.remove(force)
    }

    /**
     * Clears all forces being applied to the given Entity. i.e. stops applying any forces to the Entity.
     */
    fun clearForces() {
        this.actingForces.clear()
    }

    /**
     * Translates the given part of the boundary by the supplied deltas if it exists.
     *
     * @param boundaryName name of boundary section to translate
     * @param dx x delta to translate by
     * @param dy y delta to translate by
     */
    fun translate(boundaryName: String, dx: Double, dy: Double) {
        this.boundaries[boundaryName]?.translate(dx, dy)
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        this.boundaries.forEach { (_, boundary) ->
            boundary.render(canvas, x, y)
        }
    }

    override fun translate(dx: Double, dy: Double) {
        this.boundaries.forEach { (_, boundary) ->
            boundary.translate(dx, dy)
        }
    }

    override fun react(collisionObject: CollisionObject) {
        // Abstract Entity by default doesn't react
    }

    override fun toString(): String =
        "Entity(boundaries=${this.boundaries}, velocity=${this.velocity}, mass=${this.mass}, restitution=${this.restitution})"

}