package org.storm.physics.math.geometry.shapes

import org.storm.core.graphics.geometry.Point

/**
 * A Triangle is a ConvexPolygon with three sides and is the smallest possible ConvexPolygon.
 */
open class Triangle(a: Point, b: Point, c: Point) : Polygon(a, b, c)
