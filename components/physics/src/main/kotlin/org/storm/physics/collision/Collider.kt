package org.storm.physics.collision

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Geometric
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.CollidableShape
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * Encapsulates collision and physics data. Can be associated to another object.
 */
class Collider(
    val boundaries: MutableMap<String, CollidableShape>,
    mass: Double,
    var restitution: Double = 0.7,
    val associatedObj: Any? = null,
    var eventOnCollision: Boolean = false
) : Geometric, Renderable {

    companion object {
        private const val SINGLE_BOUNDARY = "single-boundary"
        private const val INFINITE_DURATION = Double.POSITIVE_INFINITY
    }

    var velocity: Vector = Vector.ZERO_VECTOR

    var mass: Double = 1.0
        set(value) {
            require(value > 0) { "mass must be > 0" }
            inverseMass = 1.0 / value
            field = value
        }

    var inverseMass: Double = 1.0
        private set

    val boundary: CollidableShape? get() = this.boundaries[SINGLE_BOUNDARY]

    val immovable: Boolean get() = mass == Double.POSITIVE_INFINITY || mass == Double.NEGATIVE_INFINITY

    private val forces: MutableMap<Vector, Double> = ConcurrentHashMap()

    constructor(
        boundary: CollidableShape,
        mass: Double,
        restitution: Double = 1.0,
        associatedObj: Any? = null
    ) : this(mutableMapOf(SINGLE_BOUNDARY to boundary), mass, restitution, associatedObj)

    init {
        require(this.restitution in 0.0..1.0) { "restitution must be in the range [0, 1]" }
        this.mass = mass
    }

    /**
     * Adds the force to the collider for the given duration (in seconds). If the force already
     * exists then the duration is added to the existing duration. Providing no duration value
     * defaults to infinite duration.
     *
     * @param force Vector representing the force to add
     * @param duration How long to apply the force for in seconds
     */
    fun addForce(force: Vector, duration: Double = INFINITE_DURATION) {
        this.forces.computeIfPresent(force) { _, remainingDuration ->
            remainingDuration + duration
        }
        this.forces.putIfAbsent(force, duration)
    }

    /**
     * Applies accumulated forces to update the collider's velocity.
     *
     * Converts each force to acceleration (F/m), applies it to velocity, and decrements
     * the force duration. Forces with expired durations are removed.
     *
     * @param elapsedTime The time elapsed since the last update in seconds
     */
    fun applyForces(elapsedTime: Double) {
        // Using iterator approach here to handle this in one pass
        val iterator = this.forces.entries.iterator()
        while (iterator.hasNext()) {
            val (force, remainingDuration) = iterator.next()
            val acceleration = force.scale(elapsedTime / this.mass)
            this.velocity = this.velocity.add(acceleration)

            if (remainingDuration <= elapsedTime) {
                iterator.remove()
            } else {
                this.forces[force] = remainingDuration - elapsedTime
            }
        }
    }

    /**
     * Translates the Entity's position by its current velocity
     */
    fun translateByVelocity() {
        this.translate(velocity.x, velocity.y)
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

    override fun toString(): String =
        "Entity(boundaries=${this.boundaries}, velocity=${this.velocity}, mass=${this.mass}, restitution=${this.restitution})"

}