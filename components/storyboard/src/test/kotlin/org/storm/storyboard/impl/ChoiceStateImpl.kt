package org.storm.storyboard.impl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import javafx.scene.canvas.GraphicsContext
import org.storm.core.asset.Asset
import org.storm.core.input.action.ActionManager
import org.storm.storyboard.dialogue.Choice
import org.storm.storyboard.dialogue.ChoiceState

@Asset(type = "choice")
class ChoiceStateImpl @JsonCreator constructor(
    name: String,
    choices: List<Choice>
) : ChoiceState(name, choices) {

    override fun isComplete(): Boolean {
        return choice != null
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        // Noop
    }

    override fun update(time: Double, elapsedTime: Double) {
       // Noop
    }

    override fun process(actionManager: ActionManager) {

    }
}
