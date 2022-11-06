package org.storm.animations;

import org.storm.core.render.Renderable;
import org.storm.core.update.Updatable;

/**
 * Represents a 2D Animation within the game.
 */
public interface Animation extends Renderable, Updatable {

  int LOOP_INDEFINITELY = Integer.MIN_VALUE;

  /**
   * Plays the animation
   */
  void play();

  /**
   * Pauses the animation
   */
  void pause();

  /**
   * Resets the animation
   */
  void reset();

}
