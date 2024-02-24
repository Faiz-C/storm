package org.storm.core.input

/**
 * An ActionEvent is a simple event that represents if a specific action has become active or inactive.
 */
data class ActionEvent(
    val action: String,
    val active: Boolean
)
