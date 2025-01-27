package org.storm.core.extensions

import org.storm.core.context.Context
import org.storm.core.context.UNIT_CONVERTOR

/**
 * @return the value converted to game engine units using the current PPU value
 */
val Int.units get() = Context.UNIT_CONVERTOR.toUnits(this.toDouble())

/**
 * @return the value converted to pixels using the current PPU value
 */
val Int.pixels get() = Context.UNIT_CONVERTOR.toPixels(this.toDouble())

/**
 * @return the value converted to game engine units using the current PPU value
 */
val Double.units get() = Context.UNIT_CONVERTOR.toUnits(this)

/**
 * @return the value converted to pixels using the current PPU value
 */
val Double.pixels get() = Context.UNIT_CONVERTOR.toPixels(this)

