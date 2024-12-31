package org.storm.storyboard.impl

import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.input.action.ActionState
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Color
import org.storm.core.render.canvas.Font
import org.storm.core.render.canvas.Settings
import org.storm.storyboard.dialogue.script.Script
import org.storm.storyboard.dialogue.script.player.ScriptPlayer

/**
 * This is a TEST script player that is used to simulate a dialogue box in the StoryBoardEngineTest.
 */
class TextBoxScriptPlayer(script: Script) : ScriptPlayer(script) {

    companion object {
        private const val TEXT_BOX_HEIGHT = 120.0 // pixels
        private const val TEXT_FRAME_DELAY = 2 // number of frames to wait before adding the next character to the screen
    }

    private var screenText = ""
    private var currentFrame = 0
    private var currentLength = 0

    override fun reset() {
        screenText = ""
        currentFrame = 0
        currentLength = 0
        super.reset()
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        if (currentFrame++ >= TEXT_FRAME_DELAY) {
            currentFrame = 0
            currentLength = if (currentLength + 1 < line.length) {
                currentLength + 1
            } else {
                line.length
            }
            screenText = line.substring(0, currentLength)
        }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        val (screenWidth, screenHeight) = Context.RESOLUTION

        // Draw a rectangle to wrap around the text
        canvas.drawRectWithPixels(1.0, screenHeight - TEXT_BOX_HEIGHT, screenWidth - canvas.settings.thickness, TEXT_BOX_HEIGHT - 1.0)

        // Draw a rectangle on top of the above to wrap around the speaker
        canvas.drawRectWithPixels(1.0, screenHeight - TEXT_BOX_HEIGHT - 40.0, 70.0, 40.0)

        // Draw the speaker
        canvas.withSettings(Settings(fill = true, font = Font(size = 20.0, weight = 700))) {
            it.drawTextWithPixels(speaker, 10.0, screenHeight - TEXT_BOX_HEIGHT - 17.0)
        }

        // Draw the current line of dialogue
        canvas.withSettings(Settings(fill = true, font = Font(size = 20.0))) {
            it.drawTextWithPixels(screenText, 5.0, screenHeight - TEXT_BOX_HEIGHT + 25.0)
        }

        if (scriptState != Script.State.MAKING_CHOICE) return

        // Draw choices
        script.choices.forEachIndexed { i, choice ->
            val (color, choiceText) = if (i == currentChoice) {
                Color(255.0, 0.0, 0.0, 1.0) to ">> $choice"
            } else {
                Color(255.0, 255.0, 255.0, 1.0) to "   $choice"
            }

            canvas.withSettings(Settings(fill = true, color = color)) {
                canvas.drawTextWithPixels(choiceText, 10.0, (screenHeight - TEXT_BOX_HEIGHT + 60.0) + (i * 20.0))
            }
        }
    }

    override suspend fun process(actionState: ActionState) {
        if (actionState.isFirstTrigger("progress")) {
            if (currentLength < line.length) {
                currentLength = line.length - 1
            } else if (!isDialogueComplete()) {
                currentLength = 0
                this.progress()
            } else {
                this.progress()
            }
        } else if (actionState.isFirstTrigger("up") && scriptState == Script.State.MAKING_CHOICE) {
            currentChoice = (currentChoice - 1).coerceAtLeast(0)
        } else if (actionState.isFirstTrigger("down") && scriptState == Script.State.MAKING_CHOICE) {
            currentChoice = (currentChoice + 1).coerceAtMost(script.choices.size - 1)
        }
    }
}
