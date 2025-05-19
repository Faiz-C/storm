package org.storm.physics.math

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.storm.core.graphics.geometry.Point
import org.storm.physics.math.geometry.LineSegment

class LineSegmentTest {

    @Test
    fun testVectorConversion() {
        val segment = LineSegment(1.0, 0.0, 3.0, 4.0)
        val v1 = segment.vectorFromStart
        val v2 = segment.vectorFromEnd
        Assertions.assertTrue(v1.dot(v2) < 0) // they should be in opposite directions
        Assertions.assertEquals(0.0, v1.dot(v1.counterClockwiseNormal), 0.0)
        Assertions.assertEquals(0.0, v1.dot(v2.counterClockwiseNormal), 0.0)
        Assertions.assertEquals(0.0, v1.dot(v1.clockwiseNormal), 0.0)
        Assertions.assertEquals(0.0, v1.dot(v2.clockwiseNormal), 0.0)

        val normalStart = v1.toPoint()
        val (x, y) = v1.counterClockwiseNormal.toPoint()
        val normalEnd = Point(normalStart.x + x, normalStart.y + y)

        val normalLineSegment = LineSegment(normalStart, normalEnd)
        Assertions.assertEquals(v1.counterClockwiseNormal, normalLineSegment.vectorFromStart)
    }

    @Test
    fun testClosestPoint() {
        val segment = LineSegment(2.0, 2.0, 10.0, 0.0)
        val p = Point(5.0, 5.0)
        val c = segment.getClosestPoint(p)

        Assertions.assertTrue(segment.contains(c))
    }

}
