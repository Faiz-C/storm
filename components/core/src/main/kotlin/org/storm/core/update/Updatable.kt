package org.storm.core.update

/**
 * An Updatable represents an object which can change over the span of a running game.
 */
interface Updatable {
  /**
   * Updates the object internally
   *
   * @param time the time in seconds at which the update method is called
   * @param elapsedTime the time in seconds it has been since the update method was last called
   */
  fun update(time: Double, elapsedTime: Double)
}