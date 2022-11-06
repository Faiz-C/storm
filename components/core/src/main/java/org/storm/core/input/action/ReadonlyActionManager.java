package org.storm.core.input.action;

import org.storm.core.exception.ActionManagerException;

import java.util.Map;

/**
 * An implementation of an ActionManager which only allows for checking the state of an action.
 */
public class ReadonlyActionManager extends ActionManager {

  public ReadonlyActionManager(Map<String, Boolean> state) {
    super(state);
  }

  @Override
  public void add(String action) {
    throw new ActionManagerException("add not implemented for %s", this.getClass().getName());
  }

  @Override
  public void remove(String action) {
    throw new ActionManagerException("remove not implemented for %s", this.getClass().getName());
  }

  @Override
  public void startUsing(String action) {
    throw new ActionManagerException("startUsing not implemented for %s", this.getClass().getName());
  }

  @Override
  public void stopUsing(String action) {
    throw new ActionManagerException("stopUsing not implemented for %s", this.getClass().getName());
  }

}
