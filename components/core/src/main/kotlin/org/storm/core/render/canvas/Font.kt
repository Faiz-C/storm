package org.storm.core.render.canvas

data class Font(
    val size: Double = 12.0,
    val type: String = "Arial",
    val weight: Int = 400 // Normal font weight following standard weight mapping
) {
    init {
        require(weight >= 0 && weight <= 900) {
            "weight value must be in range [0, 900]"
        }

        require(this.weight % 100 == 0) {
            "weight value of font must be a multiple of 100"
        }
    }
}