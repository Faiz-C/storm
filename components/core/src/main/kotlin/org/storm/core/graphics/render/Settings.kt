package org.storm.core.graphics.render

import org.storm.core.graphics.render.brush.Color
import org.storm.core.graphics.render.brush.Font

data class Settings(
    val thickness: Double = 2.0,
    val color: Color = Color.BLACK,
    val fill: Boolean = false,
    val font: Font = Font(),
    val alpha: Double = 1.0
)