package org.storm.storyboard.dialogue.player

import org.storm.core.input.Processor
import org.storm.core.input.action.ActionManager
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.dialogue.Script
import kotlin.math.max
import kotlin.math.min

abstract class ScriptPlayer(
    protected val script: Script
): Updatable, Renderable, Processor {

    protected var currentDialogue = 0
    protected var currentLine = 0

    protected var choiceMade = false
    protected var makingChoice = false
    protected var currentChoice = 0

    protected val speaker get() = script.dialogue[currentDialogue].speaker
    protected val line get() = script.dialogue[currentDialogue].lines[currentLine]

    val choice
        get() = if (isComplete() && isChoiceRequired()) {
            currentChoice
        } else {
            null
        }

    /**
     * Progress the dialogue to the next line or segment
     */
    open fun progress() {
        if (currentLine < script.dialogue[currentDialogue].lines.size - 1) {
            currentLine++
        } else {
            currentDialogue = min(currentDialogue + 1, script.dialogue.size - 1)
            currentLine = 0
        }

        if (!makingChoice && isDialogueComplete() && isChoiceRequired()) {
            makingChoice = true
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
        return script.choices.isNotEmpty()
    }

    /**
     * @return true if the dialogue segment of the script is complete, false otherwise
     */
    open fun isDialogueComplete(): Boolean {
        return currentDialogue == script.dialogue.size - 1 && currentLine == script.dialogue[currentDialogue].lines.size - 1
    }

    /**
     * @return true if the script has been completely traversed and all required choices have been made, false otherwise
     */
    open fun isComplete(): Boolean {
        val choiceNotRequired = script.choices.isEmpty()
        return isDialogueComplete() && (choiceNotRequired || choiceMade)
    }

}
