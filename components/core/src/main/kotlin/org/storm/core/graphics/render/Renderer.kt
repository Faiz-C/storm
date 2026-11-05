package org.storm.core.graphics.render

fun interface Renderer {
    fun render(snapshot: RenderSnapshot)
}