package org.storm.core.graphics.render

/**
 * Takes a snapshot of render commands and executes on them.
 */
fun interface Renderer {
    fun render(snapshot: RenderingBuffer)
}