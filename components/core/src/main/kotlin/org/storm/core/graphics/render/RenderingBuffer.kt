package org.storm.core.graphics.render

/**
 * A simple fixed size buffer for commands that a [org.storm.core.graphics.canvas.Canvas] will write to and a
 * [Renderer] will read from.
 */
class RenderingBuffer(size: Int = DEFAULT_SIZE) {

    companion object {
        const val DEFAULT_SIZE = 256
    }

    val commands: List<RenderCommand>
        field = ArrayList(size)

    fun add(command: RenderCommand) {
        this.commands.add(command)
    }

    fun clear() {
        this.commands.clear()
    }

}