package org.storm.physics.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.storm.core.render.geometry.Point
import org.storm.physics.math.extensions.getDistance

class PointTest {

    @Test
    fun testTranslate() {
        val point = Point(1.0, 2.0)
        assertEquals(1.0, point.x, 0.0)
        assertEquals(2.0, point.y, 0.0)

        point.translate(-2.0, 2.0)
        assertEquals(-1.0, point.x, 0.0)
        assertEquals(4.0, point.y, 0.0)
    }

    @Test
    fun testDistanceTo() {
        val p = Point(1.0, 1.0)
        val q = Point(5.0, 4.0)

        assertEquals(5.0, p.getDistance(q), 0.0)
        assertEquals(5.0, q.getDistance(p), 0.0)
    }

}
