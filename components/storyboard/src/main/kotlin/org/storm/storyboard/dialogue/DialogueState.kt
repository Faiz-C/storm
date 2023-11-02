package org.storm.storyboard.dialogue

import org.storm.storyboard.StoryBoardState

abstract class DialogueState(
    override val name: String,
    override val type: String = "dialogue",
    override val next: String? = null,
    protected val script: Script
): StoryBoardState {

    override val neighbourStates: Set<String> get() = next?.let { setOf(it) } ?: emptySet()

    override fun isComplete(): Boolean {
        return script.complete
    }
}
