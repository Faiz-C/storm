package org.storm.core.input

/**
 * Represents a generic input (e.g., mouse, keyboard, controller, etc...) that occurs within the game engine
 */
data class Input(
    val activations: Int = 1, // Number of times this input has been sent by the underlying OS
    val activeFrames: Int = 0, // Number of frames in a row within the game engine this input has been triggered
    val inDebounce: Boolean = false, // true if we are in a debounce state, false otherwise
    val timeHeld: Double, // Time the input has been held in milliseconds
    val lastUpdateTime: Double, // The last time we attempted to update the state of this input
    val rawInput: Any // The raw input this state relates to
)
