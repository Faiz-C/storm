package org.storm.physics.math

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.ConvexPolygon
import java.util.*

class ConvexPolygonTest {

    @Test
    fun testTranslate() {
        // Unit Square
        val convexPolygon = ConvexPolygon(Point(0.0, 0.0), Point(1.0, 0.0), Point(1.0, 1.0), Point(0.0, 1.0))
        convexPolygon.translate(4.0, 0.0)
        Assertions.assertEquals(Point(4.0, 0.0), convexPolygon.vertices[0])
        Assertions.assertEquals(Point(5.0, 0.0), convexPolygon.vertices[1])
        Assertions.assertEquals(Point(5.0, 1.0), convexPolygon.vertices[2])
        Assertions.assertEquals(Point(4.0, 1.0), convexPolygon.vertices[3])
    }

    @Test
    fun testGetProjection() {
        val convexPolygon = ConvexPolygon(Point(0.0, 0.0), Point(1.0, 0.0), Point(1.0, 1.0))
        val (start, end) = convexPolygon.project(Vector(0.0, 1.0))
        Assertions.assertEquals(0.0, start, 0.0)
        Assertions.assertEquals(1.0, end, 0.0)
    }

    @Test
    fun testCenter() {
        val convexPolygon = ConvexPolygon(Point(0.0, 3.0), Point(2.0, 0.0), Point(1.0, 0.0))
        Assertions.assertEquals(Point(1.0, 1.0), convexPolygon.center)
    }

    @Test
    fun testContains() {
        val testPoints = listOf(
            Point(10.0, 10.0), Point(10.0, 16.0), Point(-20.0, 10.0), Point(0.0, 10.0),
            Point(20.0, 10.0), Point(16.0, 10.0), Point(20.0, 20.0)
        )
        val square = ConvexPolygon(Point(0.0, 0.0), Point(20.0, 0.0), Point(20.0, 20.0), Point(0.0, 20.0))
        val hexagon = ConvexPolygon(
            Point(6.0, 0.0),
            Point(14.0, 0.0),
            Point(20.0, 10.0),
            Point(14.0, 20.0),
            Point(6.0, 20.0),
            Point(0.0, 10.0)
        )

        Assertions.assertTrue(square.contains(testPoints[0]))
        Assertions.assertTrue(square.contains(testPoints[1]))
        Assertions.assertFalse(square.contains(testPoints[2]))
        Assertions.assertFalse(square.contains(testPoints[3]))
        Assertions.assertTrue(square.contains(testPoints[4]))
        Assertions.assertTrue(square.contains(testPoints[5]))
        Assertions.assertFalse(square.contains(testPoints[6]))

        Assertions.assertTrue(hexagon.contains(testPoints[0]))
        Assertions.assertTrue(hexagon.contains(testPoints[1]))
        Assertions.assertFalse(hexagon.contains(testPoints[2]))
        Assertions.assertFalse(hexagon.contains(testPoints[3]))
        Assertions.assertTrue(hexagon.contains(testPoints[4]))
        Assertions.assertTrue(hexagon.contains(testPoints[5]))
        Assertions.assertFalse(hexagon.contains(testPoints[6]))
    }

}
