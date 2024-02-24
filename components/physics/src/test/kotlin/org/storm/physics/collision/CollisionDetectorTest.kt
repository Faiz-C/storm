package org.storm.physics.collision

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.storm.physics.collision.CollisionDetector.check
import org.storm.physics.collision.CollisionDetector.checkMtv
import org.storm.physics.constants.Vectors
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.physics.math.geometry.shapes.Triangle

class CollisionDetectorTest {

    @Test
    fun testCollision_rectAndRect() {
        val aabb = AABB(0.0, 0.0, 5.0, 5.0)
        val aabb2 = AABB(2.0, 3.0, 4.0, 2.0)
        val aabb3 = AABB(6.0, 2.0, 1.0, 1.0)
        val aabb4 = AABB(6.0, 0.0, 5.0, 5.0)
        Assertions.assertTrue(check(aabb, aabb2))
        Assertions.assertFalse(check(aabb, aabb3))
        Assertions.assertFalse(check(aabb, aabb4))
        Assertions.assertTrue(check(aabb4, aabb3))
    }

    @Test
    fun testCollision_rectAndTriangle() {
        val aabb = AABB(0.0, 0.0, 5.0, 5.0)
        val triangle = Triangle(Point(-2.0, -2.0), Point(4.0, 0.0), Point(0.0, 4.0))
        val aabb2 = AABB(-4.0, -2.0, 1.0, 1.0)
        Assertions.assertTrue(check(aabb, triangle))
        Assertions.assertFalse(check(triangle, aabb2))
    }

    @Test
    fun testCollision_triangleAndTriangle() {
        val triangle = Triangle(Point(0.0, 0.0), Point(4.0, 0.0), Point(0.0, 2.0))
        val triangle2 = Triangle(Point(0.0, 3.0), Point(2.0, -4.0), Point(2.0, 0.0))
        val triangle3 = Triangle(Point(-5.0, -5.0), Point(-1.0, -1.0), Point(-3.0, -2.0))
        Assertions.assertTrue(check(triangle, triangle2))
        Assertions.assertFalse(check(triangle2, triangle3))
    }

    @Test
    fun testCollisionWithMTV_rectAndRect() {
        val aabb = AABB(0.0, 0.0, 5.0, 5.0)
        val aabb2 = AABB(2.0, 3.0, 4.0, 2.0)
        val aabb3 = AABB(6.0, 0.0, 5.0, 5.0)
        Assertions.assertEquals(Vectors.ZERO_VECTOR, checkMtv(aabb, aabb3))
        val mtv = checkMtv(aabb, aabb2)

        // The math:
        // Axes are the vectors a1 = <1, 0>, a2 = <-1, 0>, a3 = <0, 1>, a4 = <0, -1>
        // Let p1 = rectangle, p2 = rectangle2 then the projections are:
        // p1a1 = (0, 5), p2a1 = (2, 6) <--- Intervals
        // p1a2 = (-5, 0), p1a2 = (-6, -2)
        // p1a3 = (0, 5), p2a3 = (3, 5)
        // p1a4 = (-5, 0), p2a4 = (-5, -3)
        // Therefore the direction vector of the force (by our algorithm) will be the a3 and the magnitude would be 2
        // which is the minimum overlap between the interval pairs. Then there is an epsilon addition
        // Note that the collision detector will always orient the mtv towards the center of the first polygon (rectangle
        // in this case)
        Assertions.assertEquals(2.0, mtv.magnitude, 0.0)

        // Apply the mtv to both polygons to move them away from each other
        val (x, y) = mtv.flip()
        aabb.translate(mtv.x, mtv.y)
        aabb2.translate(x, y)
        Assertions.assertFalse(check(aabb, aabb2))
    }

    @Test
    fun testCollisionWithMTV_rectAndTriangle() {
        val aabb = AABB(0.0, 0.0, 5.0, 5.0)
        val triangle = Triangle(Point(-2.0, -2.0), Point(4.0, 0.0), Point(0.0, 4.0))
        val mtv = checkMtv(aabb, triangle)


        // Apply the mtv to both polygons to move them away from each other
        val (x, y) = mtv.flip()
        aabb.translate(mtv.x, mtv.y)
        triangle.translate(x, y)
        Assertions.assertFalse(check(aabb, triangle))
    }

    @Test
    fun testCollisionWithMTV_circleAndCircle() {
        val c1 = Circle(0.0, 0.0, 5.0)
        val c2 = Circle(10.0, 10.0, 2.0)
        val c3 = Circle(2.0, 2.0, 5.0)
        Assertions.assertFalse(check(c1, c2))

        val mtv = checkMtv(c1, c3)
        val (x, y) = mtv.flip()
        Assertions.assertNotEquals(Vectors.ZERO_VECTOR, mtv)

        c1.translate(mtv.x, mtv.y)
        c3.translate(x, y)
        Assertions.assertFalse(check(c1, c3))
    }

    @Test
    fun testCollisionWithMTV_circleAndPolygon() {
        val r = AABB(25.0, 200.0, 300.0, 10.0)
        val c = Circle(50.0, 195.0, 10.0)
        val mtv = checkMtv(r, c)
        Assertions.assertNotEquals(Vectors.ZERO_VECTOR, mtv)

        val (x, y) = mtv.flip()
        r.translate(mtv.x, mtv.y)
        c.translate(x, y)
        Assertions.assertFalse(check(r, c))
    }
}
