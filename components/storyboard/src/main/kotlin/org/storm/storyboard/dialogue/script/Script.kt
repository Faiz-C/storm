package org.storm.storyboard.dialogue.script

import org.storm.storyboard.dialogue.Dialogue

data class Script(
  val dialogue: List<Dialogue>,
  val choices: List<String> = emptyList()
) {
  enum class State {
    READING_LINE,
    LINE_COMPLETE,
    MAKING_CHOICE,
    FULLY_COMPLETE
  }
}
