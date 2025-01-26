package org.storm.physics.math

import org.apache.commons.math3.util.FastMath
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.core.render.Renderable
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.geometry.Point
import java.util.Objects

/**
 * A Vector is an immutable representation of a mathematical vector in 2D vector space (not a geometric vector).
 * The x and y values of a Vector are arbitrary *unit* values for the 2D space. They may not be 1:1 with pixels on the
 * screen.
 */
data class Vector(
    val x: Double,
    val y: Double
) {

    companion object {
        val ZERO_VECTOR = Vector(0.0, 0.0)
        val UNIT_NORTH = Vector(0.0, -1.0)
        val UNIT_NORTH_EAST = Vector(1.0, -1.0)
        val UNIT_EAST = Vector(1.0, 0.0)
        val UNIT_SOUTH_EAST = Vector(1.0, 1.0)
        val UNIT_SOUTH = Vector(0.0, 1.0)
        val UNIT_SOUTH_WEST = Vector(-1.0, 1.0)
        val UNIT_WEST = Vector(-1.0, 0.0)
        val UNIT_NORTH_WEST = Vector(-1.0, -1.0)
    }

    constructor(start: Point, end: Point) : this(end.x - start.x, end.y - start.y)

    // The mathematical squared magnitude of this Vector
    val squaredMagnitude: Double by lazy { this.dot(this) }

    // The mathematical magnitude (length) of this Vector
    val magnitude: Double by lazy { FastMath.sqrt(this.squaredMagnitude) }

    // The normalized version of this Vector
    val normalized: Vector by lazy {
        if (this == ZERO_VECTOR) {
            ZERO_VECTOR
        } else {
            this.scale(1 / this.magnitude)
        }
    }

    // Clockwise normal (< -y, x >) of this Vector
    val clockwiseNormal: Vector by lazy { Vector(y, -x) }

    // Counterclockwise normal (< y, -x >) of this Vector
    val counterClockwiseNormal: Vector by lazy { Vector(-y, x) }

    /**
     * @param u Vector to project onto
     * @return the mathematical projection of this Vector onto Vector u
     */
    fun projection(u: Vector): Vector {
        return this.scale(dot(u) / this.squaredMagnitude)
    }

    /**
     * @param v Vector to dot product with
     * @return the mathematical dot product between the two Vectors
     */
    fun dot(v: Vector): Double {
        return x * v.x + y * v.y
    }

    /**
     * @param factor scalar factor to multiply the vector by
     * @return new Vector scaled by the given factor
     */
    fun scale(factor: Double): Vector {
        return Vector(x * factor, y * factor)
    }

    /**
     * @param newMagnitude new magnitude wanted for the Vector
     * @return new Vector with the same direction as this Vector scaled to the given magnitude
     */
    fun scaleToMagnitude(newMagnitude: Double): Vector {
        return if (this == ZERO_VECTOR) ZERO_VECTOR else scale(newMagnitude / this.magnitude)
    }

    /**
     * @return new Vector in opposite direction of this one
     */
    fun flip(): Vector {
        return scale(-1.0)
    }

    /**
     * @param angle angle in radians to rotate by
     * @return new Vector rotated by the given angle *anticlockwise* around the origin with the same magnitude
     */
    fun rotate(angle: Double): Vector {
        return this.rotate(Point(0.0, 0.0), angle)
    }

    /**
     * @param point Point to rotate around
     * @param angle angle in radians to rotate by
     * @return new Vector rotated by the given angle *anticlockwise* around the given Point with the same magnitude
     */
    fun rotate(point: Point, angle: Double): Vector {
        return Vector(
            FastMath.cos(angle) * (x - point.x) - FastMath.sin(angle) * (y - point.y) + point.x,
            FastMath.sin(angle) * (x - point.x) + FastMath.cos(angle) * (y - point.y) + point.y
        )
    }

    /**
     * @param point Point to face
     * @return new Vector which is rotated to face the given point but retains magnitude
     */
    fun rotateTo(point: Point): Vector {
        return rotate(FastMath.atan2(FastMath.abs(x - point.x), FastMath.abs(y - point.y)))
    }

    /**
     * @param v Vector to add
     * @return a new Vector which is the addition of this Vector and the given Vector
     */
    fun add(v: Vector): Vector {
        return Vector(x + v.x, y + v.y)
    }

    /**
     * @param dx x value adjustment of the vector
     * @param dy y value adjustment of the vector
     * @return a new Vector which is the addition of this Vector and the given dx and dy values
     */
    fun add(dx: Double, dy: Double): Vector {
        return Vector(x + dx, y + dy)
    }

    /**
     * @param v Vector to subtract
     * @return a new Vector which is the subtraction of this Vector and the given Vector
     */
    fun subtract(v: Vector): Vector {
        return Vector(x - v.x, y - v.y)
    }

    /**
     * @param dx x value adjustment of the vector
     * @param dy y value adjustment of the vector
     * @return a new Vector which is the subtraction of this Vector and the given dx and dy values
     */
    fun subtract(dx: Double, dy: Double): Vector {
        return Vector(x - dx, y - dy)
    }

    /**
     * @return a Point object of this Vector
     */
    fun toPoint(): Point = Point(x, y)

    operator fun plus(v: Vector): Vector = this.add(v)

    operator fun minus(v: Vector): Vector = this.subtract(v)

    operator fun times(factor: Double): Vector = this.scale(factor)

    operator fun times(vector: Vector): Double = this.dot(vector)

    // We override hashCode because we override equals. Objects.hash(...) is equivalent to the autogenerated hashCode
    override fun hashCode(): Int = Objects.hash(this.x, this.y)

    // Autogenerated
    // Normally you don't have to explicitly override equals for data classes when you want the default behaviour,
    // however, for something -0.0 and +0.0 super.equals(...) breaks and returns false when it should return true.
    // Note: +0.0 == -0.0 == true by IEEE standard
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }
}