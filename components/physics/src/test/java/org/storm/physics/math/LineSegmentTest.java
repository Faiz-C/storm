package org.storm.physics.math;

import org.junit.jupiter.api.Test;
import org.storm.physics.math.geometry.LineSegment;
import org.storm.physics.math.geometry.Point;

import static org.junit.jupiter.api.Assertions.*;

public class LineSegmentTest {

  @Test
  public void testVectorConversion() {
    LineSegment segment = new LineSegment(1, 0, 3, 4);
    Vector v1 = segment.getVectorFromStart();
    Vector v2 = segment.getVectorFromEnd();

    assertTrue(v1.dot(v2) < 0); // they should be in opposite directions
    assertEquals(0.0, v1.dot(v1.getCounterClockwiseNormal()), 0.0);
    assertEquals(0.0, v1.dot(v2.getCounterClockwiseNormal()), 0.0);

    assertEquals(0.0, v1.dot(v1.getClockwiseNormal()), 0.0);
    assertEquals(0.0, v1.dot(v2.getClockwiseNormal()), 0.0);

    Point normalStart = v1.toPoint();
    Point normalPoint = v1.getCounterClockwiseNormal().toPoint();
    Point normalEnd = new Point(normalStart.getX() + normalPoint.getX(), normalStart.getY() + normalPoint.getY());

    LineSegment normalLineSegment = new LineSegment(normalStart, normalEnd);

    assertEquals(v1.getCounterClockwiseNormal(), normalLineSegment.getVectorFromStart());
  }

  @Test
  public void testClosestPoint() {
    LineSegment segment = new LineSegment(2, 2, 10, 0);
    Point p = new Point(5, 5);

    Point c = segment.getClosestPoint(p);

    System.out.println(c);

    assertTrue(segment.contains(c));
  }

}
