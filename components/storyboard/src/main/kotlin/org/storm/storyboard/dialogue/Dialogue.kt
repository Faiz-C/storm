package org.storm.storyboard.dialogue

data class Dialogue(
    val speaker: String,
    val lines: List<String>,
    val choices: List<Choice> = emptyList()
) {
    private var position = 0

    val complete: Boolean get() = position >= lines.size

    val currentLine: String?
        get() {
            return if (position > this.lines.size) {
                null
            } else {
                lines[position]
            }
        }

    fun next() {
        position++
    }
}
