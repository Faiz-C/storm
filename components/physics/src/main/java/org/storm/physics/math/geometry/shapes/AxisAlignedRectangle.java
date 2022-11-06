package org.storm.physics.math.geometry.shapes;

import org.storm.physics.math.geometry.Point;

/**
 * An Axis Aligned Rectangle is a Polygon with four sides that is aligned with the x-axis and y-axis accordingly.
 */
public class AxisAlignedRectangle extends ConvexPolygon {

  public static final int TOP_LEFT_POINT = 0;
  public static final int TOP_RIGHT_POINT = 1;
  public static final int BOTTOM_RIGHT_POINT = 2;
  public static final int BOTTOM_LEFT_POINT = 3;

  public AxisAlignedRectangle(double x, double y, double width, double height) {
    super(
      new Point(x, y),
      new Point(x + width, y),
      new Point(x + width, y + height),
      new Point(x, y + height)
    );
  }

  /**
   * @param axisAlignedRectangle AxisAlignedRectangle to check against
   * @return true if this and the given AxisAlignedRectangle intersect
   */
  public boolean intersects(AxisAlignedRectangle axisAlignedRectangle) {
    Point thisBottomLeft = this.getVertices().get(BOTTOM_LEFT_POINT);
    Point otherBottomLeft = axisAlignedRectangle.getVertices().get(BOTTOM_LEFT_POINT);

    Point thisTopRight = this.getVertices().get(TOP_RIGHT_POINT);
    Point otherTopRight = axisAlignedRectangle.getVertices().get(TOP_RIGHT_POINT);

    return otherTopRight.getX() >= thisBottomLeft.getX() &&
      otherBottomLeft.getX() <= thisTopRight.getX() &&
      otherTopRight.getY() <= thisBottomLeft.getY() &&
      otherBottomLeft.getY() >= thisTopRight.getY();
  }

  /**
   * @param shape Shape to check
   * @return true if the given Shape is completely contained within this rectangle
   */
  public boolean contains(Shape shape) {
    return shape.getEdges().isEmpty() ? this.containsCircle((Circle) shape) : this.containsPolygon((ConvexPolygon) shape);
  }

  @Override
  public boolean contains(Point p) {
    // Axis Aligned Rectangles have a quicker way to check for point containment
    Point topLeft = this.vertices.get(TOP_LEFT_POINT);
    Point topRight = this.vertices.get(TOP_RIGHT_POINT);
    Point bottomRight = this.vertices.get(BOTTOM_RIGHT_POINT);
    return p.getX() >= topLeft.getX()
      && p.getX() <= topRight.getX()
      && p.getY() >= topLeft.getY()
      && p.getY() <= bottomRight.getY();
  }

  /**
   * @param circle Circle to check
   * @return true if the Circle is completely contained within this rectangle
   */
  private boolean containsCircle(Circle circle) {
    Point nearestVertex = this.getClosestVertex(circle.getCenter());
    return this.contains(circle.getCenter()) && nearestVertex.getSquaredDistance(circle.getCenter()) > circle.getRadius() * circle.getRadius();

  }

  /**
   * @param polygon ConvexPolygon to check
   * @return true if the ConvexPolygon is completely contained within this rectangle
   */
  private boolean containsPolygon(ConvexPolygon polygon) {
    for (Point p : polygon.getVertices()) {
      if (!this.contains(p)) return false;
    }
    return true;
  }
}
