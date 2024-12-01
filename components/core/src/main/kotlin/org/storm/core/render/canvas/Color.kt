package org.storm.core.render.canvas

data class Color(
    val red: Double,
    val green: Double,
    val blue: Double,
    val alpha: Double
) {
    init {
        require(0 <= red && red >= 255) {
            "red value must be within range [0, 255]"
        }

        require(0 <= green && green >= 255) {
            "green value must be within range [0, 255]"
        }

        require(0 <= blue && blue >= 255) {
            "blue value must be within range [0, 255]"
        }

        require(0 <= alpha && alpha >= 1) {
            "alpha value must be within range [0, 1]"
        }
    }
}