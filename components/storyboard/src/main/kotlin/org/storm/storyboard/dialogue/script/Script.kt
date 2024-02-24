package org.storm.storyboard.dialogue.script

import org.storm.storyboard.dialogue.Dialogue

/**
 * A Script is a collection of Dialogue and optionally Choices. A Script typically moves between a variety of states:
 * - READING_LINE: User is currently reading a line of dialogue
 * - LINE_COMPLETE: User has finished reading a line of dialogue
 * - MAKING_CHOICE: User is currently making a choice
 * - FULLY_COMPLETE: User has finished the script
 */
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
