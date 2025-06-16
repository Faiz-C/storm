package org.storm.core.input

/**
 * A snapshot of the current state of inputs within the game.
 */
data class InputState(
    val activeInputs: Map<String, Input>,
    val inputHistory: List<Input>,
    val inputCountSinceLastSnapshot: Int
)