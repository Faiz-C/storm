package org.storm.core.input.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * A manager which maintains the state of actions within a game through a map of "switches". Each action can be
 * either active (true) or non-active (false).
 */
@Slf4j
@AllArgsConstructor
public abstract class ActionManager {

  protected final Map<String, Boolean> state;

  /**
   * Adds an action to be used within the game
   *
   * @param action wanted action
   */
  public abstract void add(String action);

  /**
   * Removes an action that is being tracked
   *
   * @param action wanted action
   */
  public abstract void remove(String action);

  /**
   * Starts using the selected action. Action will be added if not present already
   *
   * @param action wanted action
   */
  public abstract void startUsing(String action);

  /**
   * Stops using the selected action. Action will be added if not present already
   *
   * @param action wanted action
   */
  public abstract void stopUsing(String action);

  /**
   * @param action wanted action
   * @return true if the action is active, false if action is non-active or isn't being tracked
   */
  public boolean isPerforming(String action) {
    return this.state.getOrDefault(action, false);
  }

  /**
   * @return Returns true if no actions are taking place, false otherwise
   */
  public boolean noActionsTaken() {
    return this.state
      .entrySet()
      .stream()
      .noneMatch(Map.Entry::getValue);
  }

  /**
   * @return Returns an immutable version of this ActionManager
   */
  public ReadonlyActionManager getReadonly() {
    return new ReadonlyActionManager(this.state);
  }

}
