package org.storm.physics.collision

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Geometric
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * Encapsulates collision and physics data for a game object
 */
open class Collider(
    val boundaries: MutableMap<String, CollidableShape>,
    mass: Double,
    var restitution: Double
) : Geometric, Renderable {

    companion object {
        private const val SINGLE_BOUNDARY = "single boundary"
    }

    val collisionState: MutableMap<Collider, Set<CollidableShape>> = mutableMapOf()

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

    val immovable: Boolean get() = mass == Double.POSITIVE_INFINITY || mass == Double.NEGATIVE_INFINITY

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
     * Called when the collider collides with another collidable object
     *
     * @param collider the object which collided with this one
     */
    open fun onCollision(collider: Collider) {
        // Noop
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