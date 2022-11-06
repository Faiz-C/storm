package org.storm.physics.math;

import org.junit.jupiter.api.Test;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.ConvexPolygon;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ConvexPolygonTest {

  @Test
  public void testTranslate() {
    // Unit Square
    ConvexPolygon convexPolygon = new ConvexPolygon(new Point(0,0), new Point(1, 0), new Point(1, 1), new Point(0, 1));
    convexPolygon.translate(4, 0);

    assertEquals(new Point(4, 0), convexPolygon.getVertices().get(0));
    assertEquals(new Point(5, 0), convexPolygon.getVertices().get(1));
    assertEquals(new Point(5, 1), convexPolygon.getVertices().get(2));
    assertEquals(new Point(4, 1), convexPolygon.getVertices().get(3));
  }

  @Test
  public void testGetProjection() {
    ConvexPolygon convexPolygon = new ConvexPolygon(new Point(0,0), new Point(1, 0), new Point(1, 1));
    Interval interval = convexPolygon.getProjection(new Vector(0, 1));

    assertEquals(0, interval.getStart(), 0);
    assertEquals(1, interval.getEnd(), 0);
  }

  @Test
  public void testCenter() {
    ConvexPolygon convexPolygon = new ConvexPolygon(new Point(0, 3), new Point(2, 0), new Point(1, 0));
    assertEquals(new Point(1, 1), convexPolygon.getCenter());
  }

  @Test
  public void testContains() {
    List<Point> testPoints = Arrays.asList(new Point(10, 10), new Point(10, 16), new Point(-20, 10), new Point(0, 10),
      new Point(20, 10), new Point(16, 10), new Point(20, 20));
    ConvexPolygon square = new ConvexPolygon(new Point(0,0), new Point(20, 0), new Point(20, 20), new Point(0, 20));
    ConvexPolygon hexagon = new ConvexPolygon(new Point(6,0), new Point(14, 0), new Point(20, 10), new Point(14, 20), new Point(6, 20), new Point(0, 10));

    assertTrue(square.contains(testPoints.get(0)));
    assertTrue(square.contains(testPoints.get(1)));
    assertFalse(square.contains(testPoints.get(2)));
    assertFalse(square.contains(testPoints.get(3)));
    assertTrue(square.contains(testPoints.get(4)));
    assertTrue(square.contains(testPoints.get(5)));
    assertFalse(square.contains(testPoints.get(6)));

    assertTrue(hexagon.contains(testPoints.get(0)));
    assertTrue(hexagon.contains(testPoints.get(1)));
    assertFalse(hexagon.contains(testPoints.get(2)));
    assertFalse(hexagon.contains(testPoints.get(3)));
    assertTrue(hexagon.contains(testPoints.get(4)));
    assertTrue(hexagon.contains(testPoints.get(5)));
    assertFalse(hexagon.contains(testPoints.get(6)));
  }

}
