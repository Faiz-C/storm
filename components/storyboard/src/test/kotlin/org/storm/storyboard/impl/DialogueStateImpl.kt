package org.storm.storyboard.impl

import com.fasterxml.jackson.annotation.JsonCreator
import javafx.scene.canvas.GraphicsContext
import org.storm.core.asset.Asset
import org.storm.core.input.action.ActionManager
import org.storm.storyboard.dialogue.DialogueState

@Asset(type = "dialogue")
class DialogueStateImpl @JsonCreator constructor(
    name: String,
    next: String,
    terminal: Boolean
) : DialogueState(name, next = next, terminal = terminal) {

    private var rendered: Boolean = false

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        script ?: return

        if (script?.currentSegment == null || rendered) return

        val segment = script!!.currentSegment!!

        // Renders it to the console
        val dialogue = StringBuilder()
            .appendLine(segment.character)
            .appendLine("\t${segment.dialogue}")
            .toString()

        println(dialogue)

        rendered = true
    }

    override fun update(time: Double, elapsedTime: Double) {
        // Noop
    }

    override fun process(actionManager: ActionManager) {
        // Progress based on input
    }
}
