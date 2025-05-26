package org.storm.core.graphics.geometry

import org.storm.core.graphics.Renderable

/**
 * A Geometric object can be translated (moved) in Euclidean (2D) space and is Renderable
 */
interface Geometric: Renderable {

    /**
     * Translates (moves) the Geometric object by the given deltas
     *
     * @param dx delta in the x-axis
     * @param dy delta in the y-axis
     */
    fun translate(dx: Double, dy: Double)
}