package org.storm.storyboard

import org.storm.animations.Animation
import org.storm.sound.Sound
import org.storm.storyboard.dialogue.Dialogue

data class StoryBoardDetails(
    val animation: Animation? = null,
    val sound: Sound? = null,
    val script: List<Dialogue>? = null,
)
