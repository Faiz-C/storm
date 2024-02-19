package org.storm.storyboard.dialogue

data class Script(
    val dialogue: List<Dialogue>,
    val choices: List<String> = emptyList()
)
