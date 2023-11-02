package org.storm.storyboard.dialogue

data class DialogueSegment(
    val character: String,
    private val dialogueLines: List<String>
) {

    val complete: Boolean get() = position >= dialogueLines.size

    val dialogue: String?
        get() {
            return if (position >= this.dialogueLines.size) {
                null
            } else {
                dialogueLines[position]
            }
        }

    private var position: Int = -1

    fun progress() {
        position++
    }
}
