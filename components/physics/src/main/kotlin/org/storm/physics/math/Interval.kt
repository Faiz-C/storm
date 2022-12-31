package org.storm.physics.math

import kotlin.math.max
import kotlin.math.min

/**
 * An Interval represents a mathematical interval which is inclusive on both ends (start and end).
 * The start and end values of an Interval are arbitrary *unit* values for the 2D space. They may not be 1:1 with pixels
 */
data class Interval(
  val start: Double,
  val end: Double
) {

  // length of the interval
  val length: Double = this.end - this.start

  init {
    require(end >= start) { "start must be less than end for an Interval" }
  }

  /**
   * @param interval Interval to check
   * @return true if the Intervals overlap, false otherwise
   */
  fun overlaps(interval: Interval): Boolean {
    return getOverlap(interval) > 0
  }

  /**
   * @param interval Interval to check
   * @return the amount of overlap between the Intervals
   */
  fun getOverlap(interval: Interval): Double {
    return min(end, interval.end) - max(start, interval.start)
  }
}
