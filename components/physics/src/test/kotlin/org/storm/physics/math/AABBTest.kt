package org.storm.physics.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.storm.core.graphics.geometry.Point
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.physics.math.geometry.shapes.Polygon

class AABBTest {

    private companion object {
        const val DELTA = 0.0001
    }

    @Test
    fun testIntersects() {
        val aabb = AABB(0.0, 0.0, 10.0, 10.0)
        val aabb2 = AABB(5.0, 5.0, 10.0, 10.0)
        assertTrue(aabb.intersects(aabb2))
        assertTrue(aabb2.intersects(aabb))

        val aabb3 = AABB(12.0, 12.0, 2.0, 2.0)
        assertFalse(aabb.intersects(aabb3))
        assertFalse(aabb3.intersects(aabb))
    }

    @Test
    fun testCircleBounds() {
        val circle = Circle(Point(5.0, 5.0), 5.0)
        val aabb = circle.aabbBounds()

        assertEquals(0.0, aabb.x)
        assertEquals(0.0, aabb.y)
        assertEquals(aabb.width, aabb.height)
        assertEquals(10.0, aabb.width)
    }

    @Test
    fun testPolygonBounds() {
        // Some funky polygon
        val polygon = Polygon(
            Point(20.0, 0.0),
            Point(40.0, 30.0),
            Point(20.0, 60.0),
            Point(0.0, 30.0)
        )

        val aabb = polygon.aabbBounds()
        assertEquals(0.0, aabb.x, DELTA, "minX/x mismatch")
        assertEquals(0.0, aabb.y, DELTA, "minY/y mismatch")
        assertEquals(40.0, aabb.width, DELTA, "maxX - minX/width mismatch")
        assertEquals(60.0, aabb.height, DELTA, "maxY - minY/height mismatch")
    }

}
