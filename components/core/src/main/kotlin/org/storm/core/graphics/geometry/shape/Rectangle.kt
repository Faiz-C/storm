package org.storm.core.graphics.geometry.shape

import org.storm.core.graphics.geometry.Point

/**
 * A simple class representing a Rectangle
 */
open class Rectangle(
    p1: Point,
    p2: Point,
    p3: Point,
    p4: Point
): Polygon(p1, p2, p3, p4) {

    companion object {
        const val TOP_LEFT_POINT = 0
        const val TOP_RIGHT_POINT = 1
        const val BOTTOM_RIGHT_POINT = 2
        const val BOTTOM_LEFT_POINT = 3
    }

    constructor(x: Double, y: Double, width: Double, height: Double): this(
        Point(x, y),
        Point(x + width, y),
        Point(x + width, y + height),
        Point(x, y + height)
    )

    val x get() = this.vertices[TOP_LEFT_POINT].x
    val y get() = this.vertices[TOP_LEFT_POINT].y

    val width get() = this.vertices[TOP_RIGHT_POINT].x - this.vertices[TOP_LEFT_POINT].x
    val height get() = this.vertices[BOTTOM_LEFT_POINT].y - this.vertices[TOP_LEFT_POINT].y

}