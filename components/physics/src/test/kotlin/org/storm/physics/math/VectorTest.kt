package org.storm.physics.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.storm.core.graphics.geometry.Point

class VectorTest {

    @Test
    fun testPointConstruction() {
        val (x, y) = Vector(Point(1.0, 1.0), Point(2.0, 4.0))
        assertEquals(1.0, x, 0.0)
        assertEquals(3.0, y, 0.0)
    }

    @Test
    fun testDotProduct() {
        val v1 = Vector(7.0, 12.0)
        val v2 = Vector(3.0, -2.0)
        assertEquals(-3.0, v1.dot(v2), 0.0)
    }

    @Test
    fun testGetMagnitude() {
        val vector = Vector(4.0, 3.0)
        assertEquals(5.0, vector.magnitude, 0.0)
    }

    @Test
    fun testGetNormal() {
        val vector = Vector(4.0, 3.0)
        assertEquals(Vector(-3.0, 4.0), vector.counterClockwiseNormal)
        assertEquals(Vector(3.0, -4.0), vector.clockwiseNormal)
    }

    @Test
    fun testNormalize() {
        val (x, y) = Vector(4.0, 3.0).normalized
        assertEquals(0.80, x, 0.0)
        assertEquals(0.60, y, 0.00001)
    }

    @Test
    fun testAdd() {
        val v1 = Vector(2.0, 2.0)
        val v2 = Vector(1.0, 4.0)
        assertEquals(Vector(3.0, 6.0), v1.add(v2))
        assertEquals(Vector(3.0, 6.0), v2.add(v1))
    }

    @Test
    fun testSubtract() {
        val v1 = Vector(-1.0, 5.0)
        val v2 = Vector(4.0, 12.0)
        assertEquals(Vector(-5.0, -7.0), v1.subtract(v2))
        assertEquals(Vector(5.0, 7.0), v2.subtract(v1))
    }

    @Test
    fun testScale() {
        var v = Vector(1.0, -4.0)
        v = v.scale(-4.0)
        assertEquals(-4.0, v.x, 0.0)
        assertEquals(16.0, v.y, 0.0)
    }

    @Test
    fun testScaleToMagnitude() {
        var v = Vector(4.0, 3.0)
        assertEquals(5.0, v.magnitude, 0.0)

        v = v.scaleToMagnitude(3.0)
        assertEquals(3.0, v.magnitude, 0.0)
    }

    @Test
    fun testFlip() {
        assertEquals(Vector.UNIT_WEST, Vector.UNIT_EAST.flip())
        assertEquals(Vector.UNIT_NORTH_WEST, Vector.UNIT_SOUTH_EAST.flip())
    }

    @Test
    fun testToPoint() {
        val (x, y) = Vector(1.0, 2.0).toPoint()
        assertEquals(1.0, x, 0.0)
        assertEquals(2.0, y, 0.0)
    }

    @Test
    fun testRotate() {
        val v = Vector(4.0, 3.0)
        assertEquals(5.0, v.magnitude, 0.0)

        val rotated90Degrees = v.rotate(Math.PI / 2)
        assertNotEquals(v, rotated90Degrees)
        assertEquals(5.0, rotated90Degrees.magnitude, 0.0)
    }

    @Test
    fun testRotateTo() {
        val v = Vector(4.0, 3.0)
        assertEquals(5.0, v.magnitude, 0.0)

        val rotated = v.rotateTo(Point(-2.0, -2.0))
        assertNotEquals(v, rotated)
        assertEquals(5.0, rotated.magnitude, 0.005)
    }

    @Test
    fun testEquals() {
        val v1 = Vector(2.0, 1.0)
        val v2 = Vector(1.0, 2.0)
        val v3 = Vector(2.0, 1.0)
        val v4 = Vector.UNIT_NORTH.scale(12.0)
        val v5 = Vector.UNIT_NORTH.scale(12.0)

        assertNotEquals(v1, v2)
        assertEquals(v1, v3)
        assertEquals(v4, v5)
    }

}
