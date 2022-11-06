package org.storm.physics.math;

import lombok.Getter;
import org.apache.commons.math3.util.FastMath;
import org.storm.physics.constants.Vectors;
import org.storm.physics.math.geometry.Point;

import java.util.Objects;

/**
 * A Vector is an immutable representation of a mathematical vector in 2D vector space (not a geometric vector).
 * The x and y values of a Vector are arbitrary *unit* values for the 2D space. They may not be 1:1 with pixels on the
 * screen.
 */
@Getter
public class Vector {

  private final double x;
  private final double y;

  // Purposely we don't calculate these unless needed as they are expensive and not always needed
  private double magnitude;
  private Vector normalizedForm;

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;

    this.magnitude = Double.POSITIVE_INFINITY; // Initial value as Vectors will ALWAYS have finite magnitude
  }

  public Vector(Point start, Point end) {
    this(end.getX() - start.getX(), end.getY() - start.getY());
  }

  /**
   * @return the mathematical magnitude of this Vector
   */
  public double getMagnitude() {
    if (this.magnitude == Double.POSITIVE_INFINITY) {
      this.magnitude = FastMath.sqrt(this.getSquaredMagnitude());
    }
    return this.magnitude;
  }

  /**
   * @return the mathematical squared magnitude of this Vector
   */
  public double getSquaredMagnitude() {
    return this.dot(this);
  }

  /**
   * @return normalized (unit) version of this Vector
   */
  public Vector getNormalizedForm() {
    if (this.normalizedForm == null) {
      this.normalizedForm = this.getMagnitude() == 0.0 ? Vectors.ZERO_VECTOR : this.scale(1 / this.getMagnitude());
    }
    return this.normalizedForm;
  }

  public Vector getProjection(Vector u) {
    return this.scale(this.dot(u) / this.getSquaredMagnitude());
  }

  /**
   * @return clockwise normal (<-y, x>) of this Vector
   */
  public Vector getClockwiseNormal() {
    return new Vector(-this.y, this.x);
  }

  /**
   * @return counterclockwise normal (<y, -x>) of this Vector
   */
  public Vector getCounterClockwiseNormal() {
    return new Vector(this.y, -this.x);
  }

  /**
   * @param v Vector to dot product with
   * @return the mathematical dot product between the two Vectors
   */
  public double dot(Vector v) {
    return this.x * v.getX() + this.y * v.getY();
  }

  /**
   * @param factor scalar factor to multiply the vector by
   * @return new Vector scaled by the given factor
   */
  public Vector scale(double factor) {
    return new Vector(this.x * factor, this.y * factor);
  }

  /**
   * @param newMagnitude new magnitude wanted for the Vector
   * @return new Vector with the same direction as this Vector scaled to the given magnitude
   */
  public Vector scaleToMagnitude(double newMagnitude) {
    return this.getMagnitude() == 0 ? Vectors.ZERO_VECTOR : this.scale(newMagnitude / this.getMagnitude());
  }

  /**
   * @return new Vector in opposite direction of this one
   */
  public Vector flip() {
    return this.scale(-1.0);
  }

  /**
   * @param angle angle in degrees to rotate by
   * @return new Vector rotated by the given angle *anticlockwise* from the origin with the same magnitude
   */
  public Vector rotate(double angle) {
    return new Vector(FastMath.cos(angle) * this.x - FastMath.sin(angle) * this.y,
      FastMath.sin(angle) * this.x + FastMath.cos(angle) * this.y);
  }

  /**
   * @param point Point to face
   * @return new Vector which is rotated to face the given point but retains magnitude
   */
  public Vector rotateTo(Point point) {
    return this.rotate(FastMath.atan2(FastMath.abs(this.x - point.getX()), FastMath.abs(this.y - point.getY())));
  }

  /**
   * @param v Vector to add
   * @return a new Vector which is the addition of this Vector and the given Vector
   */
  public Vector add(Vector v) {
    return new Vector(this.x + v.getX(), this.y + v.getY());
  }

  /**
   * @param dx x value adjustment of the vector
   * @param dy y value adjustment of the vector
   * @return a new Vector which is the addition of this Vector and the given dx and dy values
   */
  public Vector add(double dx, double dy) {
    return new Vector(this.x + dx, this.y + dy);
  }

  /**
   * @param v Vector to subtract
   * @return a new Vector which is the subtraction of this Vector and the given Vector
   */
  public Vector subtract(Vector v) {
    return new Vector(this.x - v.getX(), this.y - v.getY());
  }

  /**
   * @param dx x value adjustment of the vector
   * @param dy y value adjustment of the vector
   * @return a new Vector which is the subtraction of this Vector and the given dx and dy values
   */
  public Vector subtract(double dx, double dy) {
    return new Vector(this.x - dx, this.y - dy);
  }

  /**
   * @return a Point object of this Vector
   */
  public Point toPoint() {
    return new Point(this.x, this.y);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) || (obj != null && this.hashCode() == obj.hashCode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.x + 0.0, this.y + 0.0);
  }

  @Override
  public String toString() {
    return String.format("<%f, %f>", this.x, this.y);
  }
}
