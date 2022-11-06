package org.storm.physics.math.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.storm.physics.math.Vector;

import java.util.Objects;

/**
 * A Point represents a mathematical point in 2D space. The x and y values of a Point are arbitrary *unit* values for
 * the 2D space. They may not be 1:1 with pixels on the screen.
 */
@Getter
@Setter
@AllArgsConstructor
public class Point implements Geometric {

  private double x;
  private double y;

  /**
   * @param p Point to calculate distance to
   * @return distance from this Point to the given Point p
   */
  public double getDistance(Point p) {
    LineSegment lineSegment = new LineSegment(this, p);
    return lineSegment.getLength();
  }

  /**
   * @param p Point to calculate squared distance to
   * @return squared distance from this Point to the given Point p
   */
  public double getSquaredDistance(Point p) {
    LineSegment lineSegment = new LineSegment(this, p);
    return lineSegment.getSquaredLength();
  }

  /**
   * @return this Point as a Vector from the Origin
   */
  public Vector toVector() {
    return new Vector(this.x, this.y);
  }

  @Override
  public void translate(double dx, double dy) {
    this.x += dx;
    this.y += dy;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj) || (obj != null && this.hashCode() == obj.hashCode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.x + 0.0, this.y + 0.0);
  }

  @Override
  public String toString() {
    return String.format("(%f, %f)", this.x, this.y);
  }
}
