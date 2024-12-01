package org.storm.core.render.geometry

/**
 * A Point represents a point in 2D space. The x and y values of a Point are arbitrary *unit* values for
 * the 2D space. They may not be 1:1 with pixels on the screen.
 */
data class Point(
    var x: Double,
    var y: Double
) : Geometric {

    override fun translate(dx: Double, dy: Double) {
        this.x += dx
        this.y += dy
    }

}