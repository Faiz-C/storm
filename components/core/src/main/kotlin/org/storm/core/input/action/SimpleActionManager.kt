package org.storm.core.input.action

/**
 * Simple implementation of an ActionManager.
 */
class SimpleActionManager : ActionManager(HashMap()) {
  override fun add(action: String) {
    this.state[action] = false
  }

  override fun remove(action: String) {
    state.remove(action)
  }

  override fun startUsing(action: String) {
    state[action] = true
  }

  override fun stopUsing(action: String) {
    state[action] = false
  }
}
