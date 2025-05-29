package org.storm.physics.collision

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Geometric
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * Encapsulates collision and physics data. Can be associated to another object.
 */
class Collider(
    val boundaries: MutableMap<String, CollidableShape>,
    mass: Double,
    var restitution: Double = 0.7,
    val associatedObj: Any? = null
) : Geometric, Renderable {

    companion object {
        private const val SINGLE_BOUNDARY = "single boundary"
    }

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

    private val collisionHandlers: MutableList<(Collider, CollidableShape) -> Unit> = mutableListOf()

    constructor(
        boundary: CollidableShape,
        mass: Double,
        restitution: Double = 1.0,
        associatedObj: Any? = null
    ) : this(mutableMapOf(SINGLE_BOUNDARY to boundary), mass, restitution)

    init {
        require(!(this.restitution > 1 || this.restitution < 0)) { "restitution must be in the range [0, 1]" }
        this.mass = mass
    }

    /**
     * Triggers a collision between this collider and the given collider's specific boundary. This will trigger all
     * registered collision handlers.
     *
     * @param collider Collider which owns the boundary we collided with
     * @param boundary Specific boundary we collided with
     */
    fun collide(collider: Collider, boundary: CollidableShape) {
        collisionHandlers.forEach {
            it(collider, boundary)
        }
    }

    /**
     * Adds the given callback as a collision handler to be called when this collider collides with something.
     *
     * @param callback the callback function to invoke when a collision occurs
     */
    fun addOnCollisionHandler(callback: (Collider, CollidableShape) -> Unit) {
        collisionHandlers.add(callback)
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