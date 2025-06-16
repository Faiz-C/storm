package org.storm.core.input

/**
 * Represents a simple input which happens within the game engine. The type of the event is free form but is used to
 * uniquely handle the event (e.g., a keycode such as "w" or "mouse-pressed" for mouse buttons).
 */
data class InputEvent(
    val type: String, // used for uniqueness
    val input: Any, // raw input
    val active: Boolean = true // whether the input is actively being used or not
)
