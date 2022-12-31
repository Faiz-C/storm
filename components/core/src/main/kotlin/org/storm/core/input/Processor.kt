package org.storm.core.input

import org.storm.core.input.action.ActionManager

/**
 * A Processor processes the state of a given ActionManager and updates the state of the game accordingly. I.e.
 * if the state of a JUMP action is true then after processing the player could start jumping within the game.
 */
fun interface Processor {

  /**
   * Processes the given ActionManager state and updates the state of the game accordingly
   *
   * @param actionManager ActionManager holding the state to process
   */
  fun process(actionManager: ActionManager)

}
