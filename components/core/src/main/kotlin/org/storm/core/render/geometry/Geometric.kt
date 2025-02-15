package org.storm.core.render.geometry

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
}