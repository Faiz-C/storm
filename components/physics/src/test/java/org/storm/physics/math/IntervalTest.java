package org.storm.physics.math;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntervalTest {

  @Test
  public void testIllegalConstruction() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Interval(4, -4);
    });
  }

  @Test
  public void testLength() {
    Interval interval = new Interval(2, 4);
    assertEquals(2, interval.length(), 0.0);
  }

  @Test
  public void testOverlap() {
    Interval i = new Interval(0, 5);
    Interval j = new Interval(2, 4);
    Interval k = new Interval(-3, 2);
    Interval l = new Interval(-10, -4);

    assertTrue(i.overlaps(j));
    assertFalse(j.overlaps(k));
    assertFalse(k.overlaps(l));
    assertEquals(2, i.getOverlap(j), 0.0);
    assertEquals(2, i.getOverlap(k), 0.0);
  }

}
