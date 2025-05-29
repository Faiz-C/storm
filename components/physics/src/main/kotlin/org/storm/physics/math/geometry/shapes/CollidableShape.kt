package org.storm.physics.math.geometry.shapes

import org.storm.core.graphics.geometry.Geometric
import org.storm.core.graphics.geometry.Point
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector

/**
 * Represents a 2D shape that can collide with something via standard physics.
 */
interface CollidableShape : Geometric {

    /**
     * @return the center of the Shape if applicable
     */
    val center: Point

    /**
     * Calculates and returns the projection of the shape onto the given axis (in vector form). This
     * is used for collision detection.
     *
     * @param axis axis to project onto
     * @return an Interval representing the projection
     */
    fun project(axis: Vector): Interval

    /**
     * @param p Point to check
     * @return true if the given Point is contained within the shape
     */
    operator fun contains(p: Point): Boolean

}