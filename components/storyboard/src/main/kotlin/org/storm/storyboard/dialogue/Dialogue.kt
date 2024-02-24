package org.storm.storyboard.dialogue

/**
 * A Dialogue is a collection of lines spoken by a speaker
 */
data class Dialogue(
    val speaker: String,
    val lines: List<String>
)
