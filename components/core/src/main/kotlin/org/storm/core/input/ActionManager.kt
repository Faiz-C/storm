package org.storm.core.input

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * A manager which maintains the state of actions within a game through a map of "switches". Each action can be
 * either active (true) or non-active (false).
 */
open class ActionManager(
  protected val debounceTime: Duration = Duration.ofMillis(150),
  protected val maxEventHistory: Int = 10,
  protected val maxActionHistory: Int = 10
) {

  // TODO: Potentially update this to not use coroutines or make this class coroutine aware
  protected val actionMutex: Mutex = Mutex()

  protected val activeActions: MutableMap<String, ActionInputInfo> = ConcurrentHashMap()
  protected val activeActionHistory: MutableList<String> = mutableListOf()
  protected val eventHistory: MutableList<ActionEvent> = mutableListOf()

  protected var eventCountSinceLastSnapshot: Int = 0
  protected var actionCountSinceLastSnapshot: Int = 0

  fun submitActionEvent(actionEvent: ActionEvent) {
    runBlocking {
      actionMutex.withLock {
        updateEventHistory(actionEvent)

        updateActiveActions(actionEvent)

        activeActions[actionEvent.action]?.let { actionInputInfo ->
          updateActionHistory(actionEvent, actionInputInfo)
        }
      }
    }
  }

  fun getStateSnapshot(): ActionState {
    return runBlocking {
      actionMutex.withLock {
        activeActions.forEach { (action, info) ->
          activeActions[action] = info.copy(
            activeSnapshots = info.activeSnapshots + 1
          )
        }

        val actionState = ActionState(
          activeActions = activeActions,
          activeActionHistory = activeActionHistory,
          eventHistory = eventHistory,
          eventCountSinceLastSnapshot = eventCountSinceLastSnapshot,
          actionCountSinceLastSnapshot = actionCountSinceLastSnapshot
        )

        eventCountSinceLastSnapshot = 0
        actionCountSinceLastSnapshot = 0

        actionState
      }
    }
  }

  private fun updateEventHistory(actionEvent: ActionEvent) {
    eventHistory.add(actionEvent)

    if (eventHistory.size > maxEventHistory) {
      eventHistory.drop(1)
    }

    eventCountSinceLastSnapshot++
  }

  private fun updateActiveActions(actionEvent: ActionEvent) {
    if (!actionEvent.active) {
      activeActions.remove(actionEvent.action)
      return
    }

    val currentTime = System.currentTimeMillis()

    activeActions.computeIfPresent(actionEvent.action) { _, info ->
      val elapsedTime = currentTime - info.lastUpdateTime
      val inDebounce = elapsedTime < debounceTime.toMillis()
      info.copy(
        triggers = if (inDebounce) info.triggers + 1 else 1,
        inDebounce = inDebounce,
        timeHeldInMillis = info.timeHeldInMillis + elapsedTime,
        lastUpdateTime = currentTime
      )
    }

    activeActions.putIfAbsent(
      actionEvent.action,
      ActionInputInfo(inDebounce = false, timeHeldInMillis = 0L, lastUpdateTime = currentTime)
    )
  }

  private fun updateActionHistory(actionEvent: ActionEvent, actionInputInfo: ActionInputInfo) {
    if (actionInputInfo.inDebounce || !actionEvent.active) return

    activeActionHistory.add(actionEvent.action)

    if (activeActionHistory.size > maxActionHistory) {
      activeActionHistory.drop(1)
    }
  }
}
