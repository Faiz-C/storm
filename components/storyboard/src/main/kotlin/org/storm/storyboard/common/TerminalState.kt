package org.storm.storyboard.common

import javafx.scene.canvas.GraphicsContext
import org.storm.core.input.action.ActionManager
import org.storm.storyboard.StoryBoardState

data class TerminalState(
    override val name: String,
    override val type: String = "terminal"
): StoryBoardState {

    override val next: String = ""

    override val terminal: Boolean = true

    override val neighbourStates: Set<String> = emptySet()

    override fun process(actionManager: ActionManager) {
        // Noop
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        // Noop
    }

    override fun update(time: Double, elapsedTime: Double) {
        // Noop
    }

    override fun isComplete(): Boolean {
        return true
    }
}
