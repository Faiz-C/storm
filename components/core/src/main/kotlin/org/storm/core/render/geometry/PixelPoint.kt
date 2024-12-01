package org.storm.core.render.geometry

/**
 * A PixelPoint represents a point in 2D space but the x and y values of are exact *pixel* values on the screen.
 */
data class PixelPoint(
    val x: Double,
    val y: Double
)
