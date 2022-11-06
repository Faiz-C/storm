package org.storm.physics.math;

import org.junit.jupiter.api.Test;
import org.storm.physics.math.geometry.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PointTest {

  @Test
  public void testTranslate() {
    Point point = new Point(1, 2);
    assertEquals(1, point.getX(), 0);
    assertEquals(2, point.getY(), 0);

    point.translate(-2, 2);
    assertEquals(-1, point.getX(), 0);
    assertEquals(4, point.getY(), 0);
  }

  @Test
  public void testDistanceTo() {
    Point p = new Point(1, 1);
    Point q =  new Point(5, 4);

    assertEquals(5.0, p.getDistance(q), 0.0);
    assertEquals(5.0, q.getDistance(p), 0.0);
  }

}
