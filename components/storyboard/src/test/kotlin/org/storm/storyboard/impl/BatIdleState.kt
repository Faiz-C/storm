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
    override val neighbours: List<String>,
    override val details: StoryBoardDetails,
    override var disabled: Boolean = false,
) : StoryBoardState {

    // Should this really be a base class?
    // Should part of this be moved to a base class?
    // What parts of this require detailed implementation?
    // What sections can be lifted to configuration?

    private val scriptPlayer = TextBoxScriptPlayer(details.script ?: emptyList())

    override val next: String? get() = if (scriptPlayer.isChoiceRequired()) {
        scriptPlayer.choice?.let { neighbours.getOrNull(it) }
    } else {
        neighbours.firstOrNull()
    }

    override fun isComplete(): Boolean {
        val (animation, sound, _) = this.details
        return animation?.isComplete() ?: true
                && sound?.isComplete() ?: true
                && scriptPlayer.isComplete()
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        val (animation, _, _) = this.details
        animation?.render(gc, x, y)
        scriptPlayer.render(gc, x, y)
    }

    override fun update(time: Double, elapsedTime: Double) {
        val (animation, _, _) = this.details
        animation?.update(time, elapsedTime)
    }

    override fun process(actionManager: ActionManager) {
        scriptPlayer.process(actionManager)
    }
}
