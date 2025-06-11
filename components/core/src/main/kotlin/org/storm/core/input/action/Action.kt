package org.storm.core.input.action

/**
 * Action is used to store various data about action inputs. Time data is held in milliseconds.
 */
data class Action(
    val activations: Int = 1,
    val activeFrames: Int = 0, // Within the game engine this is equivalent to active frames
    val inDebounce: Boolean = false,
    val timeHeld: Long, // In Millis
    val lastUpdateTime: Long, // In Millis
    val input: Any // The original input that caused this action
)
