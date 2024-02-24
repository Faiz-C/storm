package org.storm.physics.entity

import org.storm.core.render.Renderable
import org.storm.physics.collision.Impulsive
import org.storm.physics.constants.Vectors
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.Geometric
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.Shape
import org.storm.physics.transforms.TransformableRender
import org.storm.physics.transforms.UnitConvertor

/**
 * An Entity represents an abstract object which exists in 2D space. It can have forces applied to it, collide with
 * other Entities and react when collided with. An entity experiences standard physics as well.
 */
abstract class Entity protected constructor(
    var boundaries: MutableMap<String, Shape>,
    var speed: Double,
    mass: Double,
    var restitution: Double
) : Impulsive, Geometric, TransformableRender {

    companion object {
        const val SINGLE_BOUNDARY = "single boundary"
        private const val INFINITE_DURATION = Double.NEGATIVE_INFINITY
    }

    val actingForces: MutableMap<Vector, Double> = mutableMapOf()
    val collisionState: MutableMap<Entity, Set<Shape>> = mutableMapOf()
    var velocity: Vector = Vectors.ZERO_VECTOR

    var mass: Double = 1.0
        set(value) {
            require(value > 0) { "mass must be > 0" }
            inverseMass = 1.0 / value
            field = value
        }

    var inverseMass = 0.0
        private set

    val boundary: Shape? get() = this.boundaries[SINGLE_BOUNDARY]

    constructor(
        boundary: Shape,
        speed: Double,
        mass: Double,
        restitution: Double
    ) : this(mutableMapOf(SINGLE_BOUNDARY to boundary), speed, mass, restitution)

    init {
        require(!(this.restitution > 1 || this.restitution < 0)) { "restitution must be in the range [0, 1]" }
        require(this.speed >= 0) { "speed cannot be lower than 0" }
        this.mass = mass
    }

    /**
     * Translates the Entity's position by the given velocity Vector
     *
     * @param vector velocity Vector to translate by
     */
    open fun translate(vector: Vector) {
        this.translate(vector.x, vector.y)
    }

    /**
     * Translates the Entity's position by its current velocity
     */
    open fun translateByVelocity() {
        this.translate(velocity)
    }

    /**
     * Adds the given force (in Vector form) to be applied for given duration. Applying the same force
     * more than once will result in increasing the duration in which the force will be applied for.
     *
     * @param force force to apply (as a Vector)
     * @param duration how long (in seconds) to apply the force for
     */
    /**
     * Adds the given force (in Vector form) to be applied for an infinite duration.
     *
     * @param force force to apply (as a Vector)
     */
    open fun addForce(force: Vector, duration: Double = INFINITE_DURATION) {
        this.actingForces.computeIfPresent(force) { _, remainingDuration -> remainingDuration + duration }
        this.actingForces.putIfAbsent(force, duration)
    }

    /**
     * Removes the force (in Vector form) from the given Entity. i.e. stops applying the force permanently.
     *
     * @param force force to remove/stop applying (as a Vector)
     */
    open fun removeForce(force: Vector) {
        this.actingForces.remove(force)
    }

    /**
     * Clears all forces being applied to the given Entity. i.e. stops applying any forces to the Entity.
     */
    open fun clearForces() {
        this.actingForces.clear()
    }

    /**
     * Translates the given part of the boundary by the supplied deltas if it exists.
     *
     * @param boundaryName name of boundary section to translate
     * @param dx x delta to translate by
     * @param dy y delta to translate by
     */
    open fun translate(boundaryName: String, dx: Double, dy: Double) {
        this.boundaries[boundaryName]?.translate(dx, dy)
    }

    /**
     * Rotates all boundary sections of this entity by the given angle *anticlockwise* around the given point
     *
     * @param point Point to rotate around
     * @param angle angle in radians to rotate by
     */
    open fun rotate(point: Point, angle: Double) {
        this.boundaries.forEach { (_, boundary) ->
            boundary.rotate(point, angle)
        }
    }

    override fun transform(unitConvertor: UnitConvertor) = Renderable { gc, x, y ->
        this@Entity.boundaries.forEach { (_, boundary) ->
            boundary.transform(unitConvertor).render(gc, x, y)
        }
    }

    override fun translate(dx: Double, dy: Double) {
        this.boundaries.forEach { (_, boundary) ->
            boundary.translate(dx, dy)
        }
    }

    override fun react(entity: Entity) {
        // Abstract Entity by default doesn't react
    }

    override fun toString(): String =
        "Entity(boundaries: ${this.boundaries}, speed: ${this.speed}, velocity: ${this.velocity}, mass: ${this.mass}, restitution: ${this.restitution})"

}
