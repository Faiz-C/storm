package org.storm.core.input.action

/**
 * A manager which maintains the state of actions within a game through a map of "switches". Each action can be
 * either active (true) or non-active (false).
 */
abstract class ActionManager(
  protected val state: MutableMap<String, Boolean>
) {

  // TODO: This needs to handle debouncing or clearing in a way where one input doesn't execute over multiple frames

  /**
   * Adds an action to be used within the game
   *
   * @param action wanted action
   */
  abstract fun add(action: String)

  /**
   * Removes an action that is being tracked
   *
   * @param action wanted action
   */
  abstract fun remove(action: String)

  /**
   * Starts using the selected action. Action will be added if not present already
   *
   * @param action wanted action
   */
  abstract fun startUsing(action: String)

  /**
   * Stops using the selected action. Action will be added if not present already
   *
   * @param action wanted action
   */
  abstract fun stopUsing(action: String)

  /**
   * @param action wanted action
   * @return true if the action is active, false if action is non-active or isn't being tracked
   */
  fun isActive(action: String): Boolean {
    return state[action] ?: false
  }

  /**
   * @return Returns true if no actions are taking place, false otherwise
   */
  fun noActionsTaken(): Boolean {
    return state.filterValues { it }.isEmpty()
  }

  /**
   * @return Returns an immutable version of this ActionManager
   */
  val readonly: ReadonlyActionManager get() = ReadonlyActionManager(state)
}
