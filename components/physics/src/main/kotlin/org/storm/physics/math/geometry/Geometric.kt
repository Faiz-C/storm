package org.storm.physics.math.geometry

import org.storm.physics.math.Vector

/**
 * A Geometric object can be translated (moved) in Euclidean (2D) space
 */
fun interface Geometric {

    /**
     * Translates (moves) the Geometric object by the given deltas
     *
     * @param dx delta in the x-axis
     * @param dy delta in the y-axis
     */
    fun translate(dx: Double, dy: Double)

    /**
     * Translates the Entity's position by the given Vector
     *
     * @param vector Vector to translate by
     */
    fun translate(vector: Vector) {
        translate(vector.x, vector.y)
    }
}
