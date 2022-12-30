package org.storm.core.input.action

import org.storm.core.exception.ActionManagerException

/**
 * An implementation of an ActionManager which only allows for checking the state of an action.
 */
class ReadonlyActionManager(state: MutableMap<String, Boolean>) : ActionManager(state) {
  override fun add(action: String) {
    throw ActionManagerException("add not implemented for ${this.javaClass.name}")
  }

  override fun remove(action: String) {
    throw ActionManagerException("remove not implemented for ${this.javaClass.name}")
  }

  override fun startUsing(action: String) {
    throw ActionManagerException("startUsing not implemented for ${this.javaClass.name}")
  }

  override fun stopUsing(action: String) {
    throw ActionManagerException("stopUsing not implemented for ${this.javaClass.name}")
  }
}
