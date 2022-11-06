package org.storm.physics.collision;

import org.junit.jupiter.api.Test;
import org.storm.physics.constants.Vectors;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.physics.math.geometry.shapes.Triangle;

import static org.junit.jupiter.api.Assertions.*;


public class CollisionDetectorTest {

  @Test
  public void testCollision_rectAndRect() {
    AxisAlignedRectangle axisAlignedRectangle = new AxisAlignedRectangle(0, 0, 5, 5);
    AxisAlignedRectangle axisAlignedRectangle2 = new AxisAlignedRectangle(2, 3, 4, 2);
    AxisAlignedRectangle axisAlignedRectangle3 = new AxisAlignedRectangle(6, 2, 1, 1);
    AxisAlignedRectangle axisAlignedRectangle4 = new AxisAlignedRectangle(6, 0, 5, 5);

    assertTrue(CollisionDetector.check(axisAlignedRectangle, axisAlignedRectangle2));
    assertFalse(CollisionDetector.check(axisAlignedRectangle, axisAlignedRectangle3));
    assertFalse(CollisionDetector.check(axisAlignedRectangle, axisAlignedRectangle4));
    assertTrue(CollisionDetector.check(axisAlignedRectangle4, axisAlignedRectangle3));
  }

  @Test
  public void testCollision_rectAndTriangle() {
    AxisAlignedRectangle axisAlignedRectangle = new AxisAlignedRectangle(0, 0, 5, 5);
    Triangle triangle = new Triangle(new Point(-2, -2), new Point(4, 0), new Point(0, 4));
    AxisAlignedRectangle axisAlignedRectangle2 = new AxisAlignedRectangle(-4, -2, 1, 1);

    assertTrue(CollisionDetector.check(axisAlignedRectangle, triangle));
    assertFalse(CollisionDetector.check(triangle, axisAlignedRectangle2));
  }

  @Test
  public void testCollision_triangleAndTriangle() {
    Triangle triangle = new Triangle(new Point(0, 0), new Point(4, 0), new Point(0, 2));
    Triangle triangle2 = new Triangle(new Point(0, 3), new Point(2, -4), new Point(2, 0));
    Triangle triangle3 = new Triangle(new Point(-5, -5), new Point(-1, -1), new Point(-3, -2));

    assertTrue(CollisionDetector.check(triangle, triangle2));
    assertFalse(CollisionDetector.check(triangle2, triangle3));
  }

  @Test
  public void testCollisionWithMTV_rectAndRect() {
    AxisAlignedRectangle axisAlignedRectangle = new AxisAlignedRectangle(0, 0, 5, 5);
    AxisAlignedRectangle axisAlignedRectangle2 = new AxisAlignedRectangle(2, 3, 4, 2);
    AxisAlignedRectangle axisAlignedRectangle4 = new AxisAlignedRectangle(6, 0, 5, 5);

    assertEquals(Vectors.ZERO_VECTOR, CollisionDetector.checkMtv(axisAlignedRectangle, axisAlignedRectangle4));

    Vector mtv = CollisionDetector.checkMtv(axisAlignedRectangle, axisAlignedRectangle2);

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

    assertEquals(2.0, mtv.getMagnitude(), 0.0);

    // Apply the mtv to both polygons to move them away from each other
    Vector oppDirVector = mtv.flip();
    axisAlignedRectangle.translate(mtv.getX(), mtv.getY());
    axisAlignedRectangle2.translate(oppDirVector.getX(), oppDirVector.getY());

    assertFalse(CollisionDetector.check(axisAlignedRectangle, axisAlignedRectangle2));
  }

  @Test
  public void testCollisionWithMTV_rectAndTriangle() {
    AxisAlignedRectangle axisAlignedRectangle = new AxisAlignedRectangle(0, 0, 5, 5);
    Triangle triangle = new Triangle(new Point(-2, -2), new Point(4, 0), new Point(0, 4));
    Vector mtv = CollisionDetector.checkMtv(axisAlignedRectangle, triangle);


    // Apply the mtv to both polygons to move them away from each other
    Vector oppDirVector = mtv.flip();
    axisAlignedRectangle.translate(mtv.getX(), mtv.getY());
    triangle.translate(oppDirVector.getX(), oppDirVector.getY());

    assertFalse(CollisionDetector.check(axisAlignedRectangle, triangle));
  }

  @Test
  public void testCollisionWithMTV_circleAndCircle() {
    Circle c1 = new Circle(0, 0, 5);
    Circle c2 = new Circle(10, 10, 2);
    Circle c3 = new Circle(2, 2, 5);

    assertFalse(CollisionDetector.check(c1, c2));

    Vector mtv = CollisionDetector.checkMtv(c1, c3);
    Vector mtvFlip = mtv.flip();

    assertNotEquals(Vectors.ZERO_VECTOR, mtv);

    c1.translate(mtv.getX(), mtv.getY());
    c3.translate(mtvFlip.getX(), mtvFlip.getY());

    assertFalse(CollisionDetector.check(c1, c3));
  }

  @Test
  public void testCollisionWithMTV_circleAndPolygon() {
    AxisAlignedRectangle r = new AxisAlignedRectangle(25, 200, 300, 10);
    Circle c = new Circle(50, 195, 10);

    Vector mtv = CollisionDetector.checkMtv(r, c);

    assertNotEquals(Vectors.ZERO_VECTOR, mtv);

    Vector mtvFlip = mtv.flip();
    r.translate(mtv.getX(), mtv.getY());
    c.translate(mtvFlip.getX(), mtvFlip.getY());

    assertFalse(CollisionDetector.check(r, c));
  }

}
