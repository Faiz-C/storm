package org.storm.core.graphics.render.buffered

/**
 * A simple fixed size buffer for commands that a [org.storm.core.graphics.render.RenderContext] will write to and a
 * [org.storm.core.graphics.render.Renderer] will read from.
 */
class RenderBuffer(size: Int = DEFAULT_SIZE) {

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