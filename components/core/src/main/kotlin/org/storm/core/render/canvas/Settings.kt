package org.storm.core.render.canvas

data class Settings(
    val thickness: Double = 2.0,
    val color: Color = Color.BLACK,
    val fill: Boolean = false,
    val font: Font = Font()
)