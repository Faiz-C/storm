package org.storm.storyboard.impl

import javafx.scene.canvas.GraphicsContext
import org.storm.core.asset.Asset
import org.storm.core.input.action.ActionManager
import org.storm.storyboard.StoryBoardDetails
import org.storm.storyboard.StoryBoardState

@Asset("state", "bat-idle")
class BatIdleState(
    override val id: String,
    override val type: String = "state-bat-idle",
    override val neighbours: Set<String>,
    override val details: StoryBoardDetails,
    override var disabled: Boolean = false,
) : StoryBoardState {

    override fun isComplete(): Boolean {
        val (animation, sound, dialogue) = this.details
        return animation?.isComplete() ?: true
                && sound?.isComplete() ?: true
                && dialogue?.complete ?: true
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        val (animation, _, dialogue) = this.details
        animation?.render(gc, x, y)

        dialogue?.let {
            // We'll want to do some kind of text wrapping here

            val line = StringBuilder()
                .appendLine(it.speaker)
                .appendLine("/t${it.currentLine}")
                .toString()

            gc.fillText(line, x, y, gc.canvas.width)
        }
    }

    override fun update(time: Double, elapsedTime: Double) {
        val (animation, sound, _) = this.details
        animation?.update(time, elapsedTime)

    }

    override fun process(actionManager: ActionManager) {
        val (_, _, dialogue) = this.details

        if (actionManager.isPerforming("next")) {
            dialogue?.next()
        }
    }
}
