package org.storm.storyboard.dialogue

import com.fasterxml.jackson.annotation.JsonProperty

data class DialogueSegment(
    @JsonProperty("character") val character: String,
    @JsonProperty("dialogueLines") private val dialogueLines: List<String>
) {

    val complete: Boolean get() = position >= dialogueLines.size

    val dialogue: String?
        get() {
            return if (position > this.dialogueLines.size) {
                null
            } else {
                dialogueLines[position]
            }
        }

    private var position: Int = 0

    fun progress() {
        position++
    }
}
