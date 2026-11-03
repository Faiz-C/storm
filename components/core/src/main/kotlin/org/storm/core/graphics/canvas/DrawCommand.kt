package org.storm.core.graphics.canvas

sealed interface DrawCommand {
    val settings: Settings

    data class Line(
        override val settings: Settings
    ) : DrawCommand
}