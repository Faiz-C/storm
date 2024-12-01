package org.storm.core.render

/**
 * A UnitConvertor converts arbitrary game engine units to pixels and vice versa
 */
class UnitConvertor(val ppu: Double) {

    companion object {
        val DEFAULT: UnitConvertor = UnitConvertor(10.0)
    }

    /**
     * @param units unit value to convert to pixels
     * @return given unit value converted to pixel value
     */
    fun toPixels(units: Double): Double = units * ppu

    /**
     * @param pixels pixel value to convert to units
     * @return given pixel value converted to unit value
     */
    fun toUnits(pixels: Double): Double = pixels / ppu

}