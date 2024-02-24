package org.storm.core.input

/**
 * ActionInputInfo is used to store various data about action inputs.
 */
data class ActionInputInfo(
    val triggers: Int = 1,
    val activeSnapshots: Int = 0, // Within the game engine this is equivalent to active frames
    val inDebounce: Boolean = false,
    val timeHeldInMillis: Long,
    val lastUpdateTime: Long
)
