package org.storm.physics.math;

import org.junit.jupiter.api.Test;
import org.storm.physics.constants.Vectors;
import org.storm.physics.math.geometry.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VectorTest {

  @Test
  public void testPointConstruction() {
    Vector vector = new Vector(new Point(1,1), new Point(2, 4));
    assertEquals(1, vector.getX(), 0);
    assertEquals(3, vector.getY(), 0);
  }

  @Test
  public void testDotProduct() {
    Vector v1 = new Vector(7,12);
    Vector v2 = new Vector(3, -2);

    assertEquals(-3, v1.dot(v2), 0);
  }

  @Test
  public void testGetMagnitude() {
    Vector vector = new Vector(4, 3);
    assertEquals(5, vector.getMagnitude(), 0.0);
  }

  @Test
  public void testGetNormal() {
    Vector vector = new Vector(4, 3);
    assertEquals(new Vector(-3, 4), vector.getCounterClockwiseNormal());
    assertEquals(new Vector(3, -4), vector.getClockwiseNormal());
  }

  @Test
  public void testNormalize() {
    Vector vector = new Vector(4, 3).getNormalizedForm();
    assertEquals(0.80, vector.getX(), 0);
    assertEquals(0.60, vector.getY(), 0.00001);
  }

  @Test
  public void testAdd() {
    Vector v1 = new Vector(2, 2);
    Vector v2 = new Vector(1, 4);

    assertEquals(new Vector(3, 6), v1.add(v2));
    assertEquals(new Vector(3, 6), v2.add(v1));
  }

  @Test
  public void testSubtract() {
    Vector v1 = new Vector(-1, 5);
    Vector v2 = new Vector(4, 12);

    assertEquals(new Vector(-5, -7), v1.subtract(v2));
    assertEquals(new Vector(5, 7), v2.subtract(v1));
  }

  @Test
  public void testScale() {
    Vector v = new Vector(1, -4);
    v = v.scale(-4);

    assertEquals(-4.0, v.getX(), 0.0);
    assertEquals(16.0, v.getY(), 0.0);
  }

  @Test
  public void testScaleToMagnitude() {
    Vector v = new Vector(4,  3);
    assertEquals(5, v.getMagnitude(), 0.0);
    v = v.scaleToMagnitude(3);
    assertEquals(3, v.getMagnitude(), 0.0);
  }

  @Test
  public void testFlip() {
    assertEquals(Vectors.UNIT_WEST, Vectors.UNIT_EAST.flip());
    assertEquals(Vectors.UNIT_NORTH_WEST, Vectors.UNIT_SOUTH_EAST.flip());
  }

  @Test
  public void testToPoint() {
    Point p = new Vector(1, 2).toPoint();
    assertEquals(1, p.getX(), 0.0);
    assertEquals(2, p.getY(), 0.0);
  }

  @Test
  public void testRotate() {
    Vector v = new Vector(4, 3);
    assertEquals(5, v.getMagnitude(), 0.0);

    Vector rotated90Degrees = v.rotate(Math.PI / 2);
    assertNotEquals(v, rotated90Degrees);
    assertEquals(5, rotated90Degrees.getMagnitude(), 0.0);
  }

  @Test
  public void testRotateTo() {
    Vector v = new Vector(4, 3);
    assertEquals(5, v.getMagnitude(), 0.0);

    Vector rotated = v.rotateTo(new Point(-2,-2));
    assertNotEquals(v, rotated);
    assertEquals(5, rotated.getMagnitude(), 0.005);
  }

  @Test
  public void testEquals() {
    Vector v1 = new Vector(2, 1);
    Vector v2 = new Vector(1, 2);
    Vector v3 = new Vector(2, 1);

    Vector v4 = Vectors.UNIT_NORTH.scale(12);
    Vector v5 = Vectors.UNIT_NORTH.scale(12);

    assertNotEquals(v1, v2);
    assertEquals(v1, v3);
    assertEquals(v4, v5);
  }

}
