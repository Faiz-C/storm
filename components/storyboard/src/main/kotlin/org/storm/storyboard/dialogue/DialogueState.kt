package org.storm.storyboard.dialogue

import com.fasterxml.jackson.annotation.JsonIgnore
import org.storm.storyboard.StoryBoardState

abstract class DialogueState(
    override val name: String,
    override val type: String = "dialogue",
    override val next: String = "",
    override val terminal: Boolean = next == "",
    override val neighbourStates: Set<String> = setOf(next)
): StoryBoardState {

    /**
     * The script that this DialogueState runs, this MUST be loaded by the inheriting class
     */
    protected var script: Script? = null

    override fun isComplete(): Boolean {
        return script?.complete ?: true
    }
}
