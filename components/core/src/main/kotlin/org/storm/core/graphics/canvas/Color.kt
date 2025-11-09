package org.storm.core.graphics.canvas

data class Color(
    val red: Double,
    val green: Double,
    val blue: Double,
    val alpha: Double
) {
    companion object {
        val BLACK = Color(0.0, 0.0, 0.0, 1.0)
        val WHITE = Color(255.0, 255.0, 255.0, 1.0)
    }

    init {
        require(red in 0.0..255.0) {
            "red value must be within range [0, 255]"
        }

        require(green in 0.0..255.0) {
            "green value must be within range [0, 255]"
        }

        require(blue in 0.0..255.0) {
            "blue value must be within range [0, 255]"
        }

        require(alpha in 0.0..1.0) {
            "alpha value must be within range [0, 1]"
        }
    }
}