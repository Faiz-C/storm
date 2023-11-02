package org.storm.storyboard.impl

import com.fasterxml.jackson.annotation.JsonCreator
import javafx.scene.canvas.GraphicsContext
import org.storm.core.asset.Asset
import org.storm.core.input.action.ActionManager
import org.storm.storyboard.dialogue.DialogueState
import org.storm.storyboard.dialogue.Script

@Asset(type = "dialogue")
class DialogueStateImpl @JsonCreator constructor(
    name: String,
    next: String? = null,
    script: Script
) : DialogueState(name, next = next, script = script) {

    private var rendered: Boolean = false

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        if (script.currentSegment == null || rendered) return

        val segment = script.currentSegment!!

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
