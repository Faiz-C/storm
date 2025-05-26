package org.storm.core.graphics.geometry.shape

import org.storm.core.graphics.geometry.Point

open class Rectangle(
    p1: Point,
    p2: Point,
    p3: Point,
    p4: Point
): Polygon(p1, p2, p3, p4)