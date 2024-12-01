package org.storm.core.render.canvas

data class Settings(
    val thickness: Double = 2.0,
    val color: Color = Color(255.0, 255.0, 255.0, 1.0), // Black
    val fill: Boolean = false,
    val font: Font = Font()
)