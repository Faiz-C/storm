package org.storm.animations

import org.storm.core.render.Renderable
import org.storm.core.update.Updatable

/**
 * Represents a 2D Animation within the game.
 */
interface Animation : Renderable, Updatable {
  /**
   * Plays the animation
   */
  fun play()

  /**
   * Pauses the animation
   */
  fun pause()

  /**
   * Resets the animation
   */
  fun reset()

  /**
   * @return True if the Animation is playing, false otherwise
   */
  fun isPlaying(): Boolean

  companion object {
    const val LOOP_INDEFINITELY = Int.MIN_VALUE
  }
}
