package org.storm.storyboard.dialogue

import org.storm.storyboard.StoryBoardState

abstract class ChoiceState(
    override val name: String,
    protected var choices: List<Choice>,
    override val type: String = "choice",
    override val terminal: Boolean = false,
): StoryBoardState {

    override val neighbourStates: Set<String> = choices.map { it.stateId }.toSet()

    protected var choice: Int? = null

    override val next: String get() = choices[choice!!].stateId

}
