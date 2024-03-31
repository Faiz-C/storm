package org.storm.core.context

import org.storm.core.ui.Resolution

object CoreContext {
    const val RESOLUTION = "resolution"
}

/**
 * @return The current resolution of the game window.
 */
val Context.RESOLUTION: Resolution get() = SETTINGS[CoreContext.RESOLUTION] as? Resolution ?: Resolution.SD

/**
 * Set the resolution of the game window.
 *
 * @param resolution The new resolution to set.
 * @param schedule Whether to schedule the update or apply it immediately (default false).
 */
fun Context.setResolution(resolution: Resolution, schedule: Boolean = false) {
    update(schedule) { settings ->
        settings + (CoreContext.RESOLUTION to resolution)
    }
}

