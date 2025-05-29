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
        require(0.0 <= red && red <= 255.0) {
            "red value must be within range [0, 255]"
        }

        require(0.0 <= green && green <= 255.0) {
            "green value must be within range [0, 255]"
        }

        require(0.0 <= blue && blue <= 255.0) {
            "blue value must be within range [0, 255]"
        }

        require(0.0 <= alpha && alpha <= 1.0) {
            "alpha value must be within range [0, 1]"
        }
    }
}