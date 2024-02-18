package org.storm.storyboard.impl

import javafx.scene.canvas.GraphicsContext
import org.storm.core.input.action.ActionManager
import org.storm.core.ui.Resolution
import org.storm.storyboard.dialogue.Dialogue
import org.storm.storyboard.dialogue.player.ScriptPlayer

class TextBoxScriptPlayer(script: List<Dialogue>) : ScriptPlayer(script) {

    companion object {
        const val TEXT_BOX_HEIGHT = 60.0 // pixels
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        // Because this is just for testing we can hardcode this
        val (screenWidth, screenHeight) = Resolution.SD

        // Draw a rectangle to wrap around the text
        gc.fillRoundRect(0.0, screenHeight - TEXT_BOX_HEIGHT, screenWidth, TEXT_BOX_HEIGHT, 10.0, 10.0)
    }

    override fun process(actionManager: ActionManager) {
        if (actionManager.isActive("next")) {
            this.progress()
        }
    }
}
