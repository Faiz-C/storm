package org.storm.sound

/**
 * Represents a Sound within the game. Sounds can be played, paused, stopped and have its volume adjusted
 */
interface Sound {
  /**
   * Plays the sound
   */
  fun play()

  /**
   * Pauses the sound
   */
  fun pause()

  /**
   * Stops the sound
   */
  fun stop()

  /**
   * Adjusts the volume of the sound to the given volume
   *
   * @param volume a double value in [0. 1] where 1 is 100% and 0 is 0%
   */
  fun adjustVolume(volume: Double)
}