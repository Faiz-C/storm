package org.storm.sound.context

import org.storm.core.context.Context

object SoundContext {
    const val MASTER_VOLUME = "masterVolume"
    const val BGM_VOLUME = "bgmVolume"
    const val EFFECT_VOLUME = "effectVolume"
    const val VOICE_VOLUME = "voiceVolume"
}

/**
 * @return The master volume of the game. This is a global volume that affects all sounds.
 */
val Context.MASTER_VOLUME: Double get() = SETTINGS[SoundContext.MASTER_VOLUME] as? Double ?: 1.0 // 100%

/**
 * @return The background music volume of the game. This is a global volume that affects all background music.
 */
val Context.BGM_VOLUME: Double get() = SETTINGS[SoundContext.BGM_VOLUME] as? Double ?: 1.0 // 100%

/**
 * @return The effect volume of the game. This is a global volume that affects all sound effects.
 */
val Context.EFFECT_VOLUME: Double get() = SETTINGS[SoundContext.EFFECT_VOLUME] as? Double ?: 1.0 // 100%

/**
 * @return The voice volume of the game. This is a global volume that affects all voice sounds.
 */
val Context.VOICE_VOLUME: Double get() = SETTINGS[SoundContext.VOICE_VOLUME] as? Double ?: 1.0 // 100%

/**
 * Sets the master volume of the game. This is a global volume that affects all sounds.
 *
 * @param volume the new volume for the game
 * @param schedule whether to schedule the update for the next frame
 */
fun Context.setMasterVolume(volume: Double, schedule: Boolean = false) {
    update(schedule) {
        mapOf(SoundContext.MASTER_VOLUME to volume)
    }
}

/**
 * Sets the background music volume of the game. This is a global volume that affects all background music.
 *
 * @param volume the new volume for the background music
 * @param schedule whether to schedule the update for the next frame
 */
fun Context.setBgmVolume(volume: Double, schedule: Boolean = false) {
    update(schedule) {
        mapOf(SoundContext.BGM_VOLUME to volume)
    }
}

/**
 * Sets the effect volume of the game. This is a global volume that affects all sound effects.
 *
 * @param volume the new volume for the sound effects
 * @param schedule whether to schedule the update for the next frame
 */
fun Context.setEffectVolume(volume: Double, schedule: Boolean = false) {
    update(schedule) {
        mapOf(SoundContext.EFFECT_VOLUME to volume)
    }
}

/**
 * Sets the voice volume of the game. This is a global volume that affects all voice sounds.
 *
 * @param volume the new volume for the voice sounds
 * @param schedule whether to schedule the update for the next frame
 */
fun Context.setVoiceVolume(volume: Double, schedule: Boolean = false) {
    update(schedule) {
        mapOf(SoundContext.VOICE_VOLUME to volume)
    }
}
