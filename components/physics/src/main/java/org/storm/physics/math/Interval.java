package org.storm.physics.math;

import lombok.Getter;

/**
 * An Interval represents a mathematical interval which is inclusive on both ends (start and end).
 * The start and end values of an Interval are arbitrary *unit* values for the 2D space. They may not be 1:1 with pixels
 */
@Getter
public class Interval {

  private final double start;
  private final double end;

  public Interval(double start, double end) {
    if (end < start) {
      throw new IllegalArgumentException("start must be less than end for an Interval");
    }
    this.start = start;
    this.end = end;
  }

  /**
   * @param interval Interval to check
   * @return true if the Intervals overlap, false otherwise
   */
  public boolean overlaps(Interval interval) {
    return this.getOverlap(interval) > 0;
  }

  /**
   * @param interval Interval to check
   * @return the amount of overlap between the Intervals
   */
  public double getOverlap(Interval interval) {
    return Math.min(this.end, interval.getEnd()) - Math.max(this.start, interval.getStart());
  }

  /**
   * @return length of the interval
   */
  public double length() {
    return this.end - this.start;
  }

  @Override
  public String toString() {
    return String.format("[%f, %f]", this.start, this.end);
  }
}
