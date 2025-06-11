package org.storm.core.input.action

/**
 * An ActionEvent is a simple event that represents if a specific action has become active or inactive.
 */
data class ActionEvent(
    val action: String,
    val active: Boolean,
    val input: Any
)
