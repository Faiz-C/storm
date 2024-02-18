package org.storm.storyboard.dialogue

data class Dialogue(
    val speaker: String,
    val lines: List<String>,
    val choices: List<String> = emptyList()
)
