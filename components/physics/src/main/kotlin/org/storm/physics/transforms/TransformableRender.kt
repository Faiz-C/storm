package org.storm.physics.transforms

import org.storm.core.render.Renderable

/**
 * A TransformableRender is an object which can be transformed from physics units to a Renderable version using pixels.
 */
interface TransformableRender {

    /**
     * @param unitConvertor the UnitConvertor to use for conversion from units to pixels
     * @return a Renderable which represents the original Physics object using pixels
     */
    fun transform(unitConvertor: UnitConvertor): Renderable

    /**
     * This uses a default unit conversion formulas of pixels = units * 10 and units = pixels / 10
     * @return a Renderable which represents the original Physics object using pixels
     */
    fun transform(): Renderable = this.transform(object : UnitConvertor {}) // Default conversion is 1 unit = 10 pixel

}
