package org.storm.storyboard.dialogue

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Script (
    @JsonProperty("dialogueSegments") private val dialogueSegments: List<DialogueSegment>
) {

    val complete: Boolean get() = position >= dialogueSegments.size

    val currentSegment: DialogueSegment?
        get() {
            return if (position >= this.dialogueSegments.size) {
                null
            } else {
                dialogueSegments[position]
            }
        }

    @JsonIgnore private var position: Int = 0

    fun progress() {
        val currentSegment = dialogueSegments[position]

        currentSegment.progress()

        if (currentSegment.complete) {
            position++
        }
    }

}
