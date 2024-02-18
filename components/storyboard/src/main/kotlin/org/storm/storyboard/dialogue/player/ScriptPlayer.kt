package org.storm.storyboard.dialogue.player

import org.storm.core.input.Processor
import org.storm.core.render.Renderable
import org.storm.storyboard.dialogue.Dialogue
import kotlin.math.max

abstract class ScriptPlayer(
    private val script: List<Dialogue>
): Renderable, Processor {

    protected var currentDialogue = 0
    protected var currentLine = 0

    protected var choiceMade = false
    protected var currentChoice = 0

    protected val speaker get() = script[currentDialogue].speaker
    protected val line get() = script[currentDialogue].lines[currentLine]

    val choice
        get() = if (isComplete() && isChoiceRequired()) {
            currentChoice
        } else {
            null
        }

    init {
        require(script.filter { it.choices.isNotEmpty() }.size == 1) {
            "There must only be one choice is the script segment"
        }
    }

    /**
     * Progress the dialogue to the next line or segment
     */
    open fun progress() {
        if (currentLine < script[currentDialogue].lines.size - 1) {
            currentLine++
        } else {
            currentDialogue = max(currentDialogue + 1, script.size - 1)
            currentLine = 0
        }
    }

    /**
     * Reset the script player to the beginning of the script
     */
    open fun reset() {
        currentDialogue = 0
        currentLine = 0
    }

    /**
     * @return true if the script requires a player choice, false otherwise
     */
    open fun isChoiceRequired(): Boolean {
        return script.lastOrNull()?.choices?.isNotEmpty() ?: false
    }

    /**
     * @return true if the script has been completely traversed and all required choices have been made, false otherwise
     */
    open fun isComplete(): Boolean {
        val lastDialogue = currentDialogue == script.size - 1
        val lastLine = currentLine == script[currentDialogue].lines.size - 1
        val choiceNotRequired = script[currentDialogue].choices.isEmpty()

        return lastDialogue && lastLine && (choiceNotRequired || choiceMade)
    }

}
