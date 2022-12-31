package org.storm.physics.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IntervalTest {
  @Test
  fun testIllegalConstruction() {
    assertThrows<IllegalArgumentException> { Interval(4.0, -4.0) }
  }

  @Test
  fun testLength() {
    val interval = Interval(2.0, 4.0)
    assertEquals(2.0, interval.length, 0.0)
  }

  @Test
  fun testOverlap() {
    val i = Interval(0.0, 5.0)
    val j = Interval(2.0, 4.0)
    val k = Interval(-3.0, 2.0)
    val l = Interval(-10.0, -4.0)

    assertTrue(i.overlaps(j))
    assertFalse(j.overlaps(k))
    assertFalse(k.overlaps(l))

    assertEquals(2.0, i.getOverlap(j), 0.0)
    assertEquals(2.0, i.getOverlap(k), 0.0)
  }
}
