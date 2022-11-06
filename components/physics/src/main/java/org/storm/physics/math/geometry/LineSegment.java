package org.storm.physics.math.geometry;

import lombok.Getter;
import org.apache.commons.math3.util.FastMath;
import org.storm.physics.math.Vector;

import java.util.Objects;

/**
 * Represents a mathematical line segment in 2D space.
 */
@Getter
public class LineSegment implements Geometric {

  private final Point start;
  private final Point end;

  private final Vector vectorFromStart;
  private final Vector vectorFromEnd;

  private final double length;

  public LineSegment(Point start, Point end) {
    this.start = new Point(start.getX(), start.getY());
    this.end = new Point(end.getX(), end.getY());
    this.vectorFromStart = new Vector(this.start, this.end);
    this.vectorFromEnd = new Vector(this.end, this.start);
    this.length = FastMath.sqrt(this.getSquaredLength());
  }

  public LineSegment(double x1, double y1, double x2, double y2) {
    this(new Point(x1, y1), new Point(x2, y2));
  }

  /**
   * @return the squared length of this LineSegment
   */
  public double getSquaredLength() {
    double xDiff = this.end.getX() - this.start.getX();
    double yDiff = this.end.getY() - this.start.getY();
    return xDiff * xDiff + yDiff * yDiff;
  }

  /**
   * @param p Point to check
   * @return Point on this LineSegment closest to p
   */
  public Point getClosestPoint(Point p)  {
    Vector startToP = new Vector(this.start, p);
    Vector projectionVector = this.getVectorFromStart().getProjection(startToP);

    return new Point(this.start.getX() + projectionVector.getX(), this.start.getY() + projectionVector.getY());
  }

  /**
   * @param p Point to check
   * @return true if p is on the LineSegment, false otherwise
   */
  public boolean contains(Point p) {
    return this.start.getDistance(p) + p.getDistance(this.end) == this.length;
  }

  @Override
  public void translate(double dx, double dy) {
    this.start.translate(dx, dy);
    this.end.translate(dx, dy);
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj) || (obj != null && this.hashCode() == obj.hashCode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.start, this.end) + Objects.hash(this.end, this.start);
  }

  @Override
  public String toString() {
    return String.format("%s to %s", this.start, this.end);
  }
}
