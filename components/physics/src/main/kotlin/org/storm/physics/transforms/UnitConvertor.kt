package org.storm.physics.transforms

/**
 * A UnitConvertor converts arbitrary physics units to pixels and vice versa
 */
interface UnitConvertor {

    /**
     * @param units unit value to convert to pixels
     * @return given unit value converted to pixel value
     */
    fun toPixels(units: Double): Double = units * 10

    /**
     * @param pixels pixel value to convert to units
     * @return given pixel value converted to unit value
     */
    fun toUnits(pixels: Double): Double = pixels / 10

}
