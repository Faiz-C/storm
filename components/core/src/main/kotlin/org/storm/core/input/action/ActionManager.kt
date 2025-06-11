package org.storm.core.input.action

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap

/**
 * A manager which maintains the state of actions within a game through a map of "switches". Each action can be
 * either active (true) or inactive (false).
 */
class ActionManager(
    private val debounceTime: Duration = Duration.ofMillis(150),
    private val maxEventHistory: Int = 5,
    private val maxActionHistory: Int = 5
) {

    private val actionMutex: Mutex = Mutex()

    private val activeActions: MutableMap<String, Action> = ConcurrentHashMap()
    private val activeActionHistory: MutableList<String> = LinkedList<String>() // want fast removal from head
    private val eventHistory: MutableList<ActionEvent> = LinkedList<ActionEvent>() // want fast removal from head

    private var eventCountSinceLastSnapshot: Int = 0
    private var actionCountSinceLastSnapshot: Int = 0

    /**
     * Submits an action event to the manager. This will update the state of active actions, event history and action history.
     *
     * @param actionEvent The action event to submit
     */
    suspend fun submitActionEvent(actionEvent: ActionEvent) {
        actionMutex.withLock {
            updateEventHistory(actionEvent)

            updateActiveActions(actionEvent)

            activeActions[actionEvent.action]?.let { actionInputInfo ->
                if (actionInputInfo.inDebounce) return@withLock

                updateActionHistory(actionEvent)
            }
        }
    }

    /**
     * Updates the active frame counts of all active actions
     */
    suspend fun updateActiveFrames() {
        actionMutex.withLock {
            activeActions.forEach { (action, info) ->
                activeActions[action] = info.copy(
                    activeFrames = info.activeFrames + 1
                )
            }
        }
    }

    /**
     * Resets the state of the ActionManager
     */
    suspend fun reset() {
        actionMutex.withLock {
            activeActions.clear()
            activeActionHistory.clear()
            eventHistory.clear()
            eventCountSinceLastSnapshot = 0
            actionCountSinceLastSnapshot = 0
        }
    }

    /**
     * @return A snapshot of the current state of the action manager
     */
    suspend fun getStateSnapshot(): ActionState {
        return actionMutex.withLock {
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

    /**
     * Updates the event history based on the given action event
     *
     * @param actionEvent The action event to update the history with
     */
    private fun updateEventHistory(actionEvent: ActionEvent) {
        eventHistory.add(actionEvent)

        if (eventHistory.size > maxEventHistory) {
            eventHistory.removeFirst()
        }

        eventCountSinceLastSnapshot++
    }

    /**
     * Updates the active actions based on the given action event
     *
     * @param actionEvent The action event to update the active actions with
     */
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
                activations = if (inDebounce) info.activations + 1 else 1,
                inDebounce = inDebounce,
                timeHeld = info.timeHeld + elapsedTime,
                lastUpdateTime = currentTime
            )
        }

        activeActions.putIfAbsent(
            actionEvent.action,
            Action(inDebounce = false, timeHeld = 0L, lastUpdateTime = currentTime, input = actionEvent.input)
        )
    }

    /**
     * Updates the action history based on the given action event
     *
     * @param actionEvent The action event to update the history with
     */
    private fun updateActionHistory(actionEvent: ActionEvent) {
        if (!actionEvent.active) return

        activeActionHistory.add(actionEvent.action)

        if (activeActionHistory.size > maxActionHistory) {
            activeActionHistory.removeFirst()
        }
    }
}
