package org.storm.core.input.action;

import java.util.HashMap;

/**
 * Simple implementation of an ActionManager.
 */
public class SimpleActionManager extends ActionManager {

  public SimpleActionManager() {
    super(new HashMap<>());
  }

  @Override
  public void add(String action) {
    this.state.put(action, false);
  }

  @Override
  public void remove(String action) {
    this.state.remove(action);
  }

  @Override
  public void startUsing(String action) {
    this.state.put(action, true);
  }

  @Override
  public void stopUsing(String action) {
    this.state.put(action, false);
  }

}
