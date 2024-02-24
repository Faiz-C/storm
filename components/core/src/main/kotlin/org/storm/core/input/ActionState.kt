package org.storm.core.input

import kotlin.math.min

data class ActionState(
    val activeActions: Map<String, ActionInputInfo>,
    val activeActionHistory: List<String>,
    val eventHistory: List<ActionEvent>,
    val eventCountSinceLastSnapshot: Int,
    val actionCountSinceLastSnapshot: Int
) {

    val eventsSinceLastSnapshot: List<ActionEvent>
        get() = eventHistory.subList(
            min(0, eventHistory.size - eventCountSinceLastSnapshot),
            eventHistory.size
        )

    val actionsSinceLastSnapshot: List<String>
        get() = activeActionHistory.subList(
            min(0, activeActionHistory.size - actionCountSinceLastSnapshot),
            activeActionHistory.size
        )

    /**
     * @param action The action to check
     * @return true if the action is active and it has only been triggered once
     */
    fun isFirstTrigger(action: String): Boolean {
        return activeActions[action]?.let { it.triggers == 1 && it.activeSnapshots == 1 } ?: false
    }

    /**
     * @param actions List of actions to check
     * @return true if all actions in the list are active
     */
    fun isCombinationPresent(actions: List<String>): Boolean {
        return actions.all { isFirstTrigger(it) }
    }

    /**
     * @param action The action to check
     * @param activeFrameThreshold Minimum required active snapshots to be considered held
     * @return true if the action is held for at least the given threshold in milliseconds
     */
    fun isActionHeld(action: String, activeFrameThreshold: Int = 2): Boolean {
        return activeActions[action]?.let {
            it.activeSnapshots >= activeFrameThreshold
        } ?: false
    }

    /**
     * @return true if there are no active actions
     */
    fun noActiveActions(): Boolean {
        return activeActions.isEmpty()
    }
}
