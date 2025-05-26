package org.storm.core.graphics.geometry.shape

import org.storm.core.graphics.geometry.Point

/**
 * A simple graphical implementation of a Circle
 */
open class Circle(
    center: Point,
    val radius: Double,
): Ellipse(center, radius * 2, radius * 2) {

    constructor(x: Double, y: Double, radius: Double) : this(Point(x, y), radius)

    override fun toString(): String = "Circle(center = $center, radius: $radius)"

}
