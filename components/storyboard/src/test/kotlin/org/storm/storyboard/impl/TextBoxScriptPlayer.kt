package org.storm.storyboard.impl

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import org.storm.core.input.action.ActionManager
import org.storm.core.ui.Resolution
import org.storm.storyboard.dialogue.Script
import org.storm.storyboard.dialogue.player.ScriptPlayer

/**
 * This is a TEST script player that is used to simulate a dialogue box in the StoryBoardEngineTest.
 */
class TextBoxScriptPlayer(script: Script) : ScriptPlayer(script) {

    companion object {
        private const val TEXT_BOX_HEIGHT = 120.0 // pixels
        private const val TEXT_FRAME_DELAY = 10 // number of frames to wait before adding the next character to the screen
    }

    private var screenText = ""
    private var currentFrame = 0
    private var currentLength = 0

    override fun update(time: Double, elapsedTime: Double) {
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

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        // Because this is just for testing we can hardcode this
        val (screenWidth, screenHeight) = Resolution.SD

        gc.lineWidth = 2.0
        gc.fill = Color.BLACK

        // Draw a rectangle to wrap around the text
        gc.strokeRect(1.0, screenHeight - TEXT_BOX_HEIGHT, screenWidth - gc.lineWidth, TEXT_BOX_HEIGHT - 1.0)

        // Draw a rectangle on top of the above to wrap around the speaker
        gc.strokeRect(1.0, screenHeight - TEXT_BOX_HEIGHT - 40.0, 70.0, 40.0)

        gc.fontSmoothingType = FontSmoothingType.LCD

        // Draw the speaker
        gc.font = Font.font("Arial", FontWeight.BOLD, 20.0)
        gc.fillText(speaker, 10.0, screenHeight - TEXT_BOX_HEIGHT - 17.0)

        gc.font = Font.font("Arial", FontWeight.NORMAL, 20.0)

        // Draw the current line of dialogue
        gc.fillText(screenText, 5.0, screenHeight - TEXT_BOX_HEIGHT + 25.0)

        if (!makingChoice) return

        // Draw choices
        script.choices.forEachIndexed { i, choice ->
            val (fill, choiceText) = if (i == currentChoice) {
                Color.RED to ">> $choice"
            } else {
                Color.BLACK to "   $choice"
            }

            gc.fill = fill
            gc.fillText(choiceText, 10.0, (screenHeight - TEXT_BOX_HEIGHT + 60.0) + (i * 20.0))
        }
    }

    override fun process(actionManager: ActionManager) {
        if (actionManager.isActive("progress") && !makingChoice) {
            if (currentLength < line.length) {
                currentLength = line.length - 1
            } else {
                currentLength = 0
                this.progress()
            }
        } else if (actionManager.isActive("up") && makingChoice) {
            currentChoice = (currentChoice - 1).coerceAtLeast(0)
        } else if (actionManager.isActive("down") && makingChoice) {
            currentChoice = (currentChoice + 1).coerceAtMost(script.choices.size - 1)
        } else if (actionManager.isActive("progress") && makingChoice) {
            choiceMade = true
        }
    }
}
