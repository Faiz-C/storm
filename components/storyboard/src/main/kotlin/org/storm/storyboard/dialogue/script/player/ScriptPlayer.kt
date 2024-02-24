package org.storm.storyboard.dialogue.script.player

import org.storm.core.input.ActionStateProcessor
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.dialogue.script.Script
import kotlin.math.min

/**
 * A ScriptPlayer is a state machine which can be used to navigate between different states of a script.
 */
abstract class ScriptPlayer(
    protected val script: Script
) : Updatable, Renderable, ActionStateProcessor {

    protected var currentDialogue = 0
    protected var currentLine = 0
    protected var currentChoice = 0
    protected var scriptState = getInitialState()

    protected val speaker get() = script.dialogue[currentDialogue].speaker
    protected val line get() = script.dialogue[currentDialogue].lines[currentLine]

    val choice
        get() = if (isComplete() && isChoiceRequired()) {
            currentChoice
        } else {
            null
        }

    /**
     * Progress the dialogue to the next state
     */
    open fun progress() {
        when {
            scriptState == Script.State.READING_LINE -> {
                currentLine = min(currentLine + 1, script.dialogue[currentDialogue].lines.size - 1)
                scriptState = Script.State.LINE_COMPLETE
            }

            scriptState == Script.State.LINE_COMPLETE && isDialogueComplete() && isChoiceRequired() -> {
                scriptState = Script.State.MAKING_CHOICE
            }

            scriptState == Script.State.LINE_COMPLETE && isDialogueComplete() -> {
                scriptState = Script.State.FULLY_COMPLETE
            }

            scriptState == Script.State.LINE_COMPLETE && isDialogueSegmentComplete() -> {
                scriptState = Script.State.READING_LINE
                currentDialogue++
                currentLine = 0
            }

            scriptState == Script.State.LINE_COMPLETE -> {
                scriptState = Script.State.READING_LINE
            }

            scriptState == Script.State.MAKING_CHOICE -> {
                scriptState = Script.State.FULLY_COMPLETE
            }

            else -> {}
        }
    }

    /**
     * Reset the script player to the beginning of the script
     */
    open fun reset() {
        currentLine = 0
        currentDialogue = 0
        currentChoice = 0
        scriptState = getInitialState()
    }

    /**
     * @return true if the script requires a player choice, false otherwise
     */
    open fun isChoiceRequired(): Boolean {
        return script.choices.isNotEmpty()
    }

    /**
     * @return true if all dialogue of the script is complete, false otherwise
     */
    open fun isDialogueComplete(): Boolean {
        return isDialogueSegmentComplete() && currentDialogue == script.dialogue.size - 1
    }

    /**
     * @return true if the current dialogue segment is complete, false otherwise
     */
    open fun isDialogueSegmentComplete(): Boolean {
        return currentLine >= script.dialogue[currentDialogue].lines.size - 1
    }

    /**
     * @return true if the script has been completely traversed and all required choices have been made, false otherwise
     */
    open fun isComplete(): Boolean {
        return scriptState == Script.State.FULLY_COMPLETE
    }

    /**
     * @return the initial state of the script
     */
    protected fun getInitialState(): Script.State {
        return when {
            // intentionally forcing a call to progress to go to MAKING_CHOICE to avoid instant MAKING_CHOICE behavior
            isDialogueComplete() && isChoiceRequired() -> Script.State.LINE_COMPLETE

            // if the dialogue is complete and no choice is required, the script is FULLY_COMPLETE
            isDialogueComplete() -> Script.State.FULLY_COMPLETE

            // otherwise we start at READING_LINE
            else -> Script.State.READING_LINE
        }
    }
}
