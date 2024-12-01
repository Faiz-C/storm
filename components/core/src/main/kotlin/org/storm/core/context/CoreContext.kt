package org.storm.core.context

import org.storm.core.extensions.units
import org.storm.core.render.UnitConvertor
import org.storm.core.ui.Resolution

object CoreContext {
    const val RESOLUTION = "resolution"
    const val UNIT_CONVERTOR = "unitConvertor"
}

/**
 * @return The current unit convertor used for converting between pixels and game engine units.
 */
val Context.UNIT_CONVERTOR get() = SETTINGS[CoreContext.UNIT_CONVERTOR] as? UnitConvertor ?: UnitConvertor.DEFAULT


/**
 * @return The current resolution of the game window.
 */
val Context.RESOLUTION: Resolution get() = SETTINGS[CoreContext.RESOLUTION] as? Resolution ?: Resolution.SD

/**
 * @return The current resolution of the game window in game units
 */
val Context.RESOLUTION_IN_UNITS: Resolution get() = Resolution(RESOLUTION.width.units, RESOLUTION.height.units)


/**
 * Set the resolution of the game window.
 *
 * @param resolution The new resolution to set.
 * @param schedule Whether to schedule the update or apply it immediately (default false).
 */
fun Context.setResolution(resolution: Resolution, schedule: Boolean = false) {
    if (resolution == this.RESOLUTION) return

    update(schedule) { settings ->
        settings + (CoreContext.RESOLUTION to resolution)
    }
}

/**
 * Sets the number of pixels per game engine unit. This is used to convert between pixels and units.
 * This change will ALWAYS be scheduled to the beginning of the next game loop to avoid potential race conditions. Avoid
 * updating this value dynamically within your game as it has implications on physics and rendering. It should be set once.
 *
 * @param ppu the number of pixels per unit
 */
fun Context.setPPU(ppu: Double) {
    update(true) { settings ->
        settings + (CoreContext.UNIT_CONVERTOR to UnitConvertor(ppu))
    }
}