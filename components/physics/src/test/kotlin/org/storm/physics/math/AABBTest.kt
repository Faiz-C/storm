package org.storm.physics.math

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.storm.physics.math.geometry.shapes.AABB

class AABBTest {

    @Test
    fun testIntersects() {
        val aabb = AABB(0.0, 0.0, 10.0, 10.0)
        val aabb2 = AABB(5.0, 5.0, 10.0, 10.0)
        Assertions.assertTrue(aabb.intersects(aabb2))
        Assertions.assertTrue(aabb2.intersects(aabb))

        val aabb3 = AABB(12.0, 12.0, 2.0, 2.0)
        Assertions.assertFalse(aabb.intersects(aabb3))
        Assertions.assertFalse(aabb3.intersects(aabb))
    }

}
