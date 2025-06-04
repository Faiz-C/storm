package org.storm.core.sound

import org.storm.core.context.BGM_VOLUME
import org.storm.core.context.Context
import org.storm.core.context.EFFECT_VOLUME
import org.storm.core.context.MASTER_VOLUME
import org.storm.core.context.SoundContext
import org.storm.core.context.VOICE_VOLUME
import org.storm.core.context.getContextEventStream
import org.storm.core.event.EventManager
import org.storm.core.exception.SoundException

/**
 * A SoundManager is a helpful way of tracking and using sounds within a game
 */
class SoundManager(
    private val sounds: MutableMap<String, Sound> = mutableMapOf(),
) {

    init {
        EventManager.getContextEventStream().addConsumerAsync {
            if (it.hasSettingChanged(SoundContext.BGM_VOLUME) || it.hasSettingChanged(SoundContext.MASTER_VOLUME)) {
                adjustAllVolume(Context.BGM_VOLUME * Context.MASTER_VOLUME, Sound.Type.BGM.value)
            }

            if (it.hasSettingChanged(SoundContext.EFFECT_VOLUME) || it.hasSettingChanged(SoundContext.MASTER_VOLUME)) {
                adjustAllVolume(Context.EFFECT_VOLUME * Context.MASTER_VOLUME, Sound.Type.EFFECT.value)
            }

            if (it.hasSettingChanged(SoundContext.VOICE_VOLUME) || it.hasSettingChanged(SoundContext.MASTER_VOLUME)) {
                adjustAllVolume(Context.VOICE_VOLUME * Context.MASTER_VOLUME, Sound.Type.EFFECT.value)
            }
        }
    }

    /**
     * Adds the given sound to the SoundManager under the given id
     *
     * @param id the id reference for the Sound
     * @param sound Sound to track
     */
    fun add(id: String, sound: Sound) {
        sounds[id] = sound
    }

    /**
     * Removes the sound associated to the given id reference
     *
     * @param id the id reference for the Sound
     */
    fun remove(id: String) {
        sounds.remove(id)
    }

    /**
     * Plays the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     */
    fun play(id: String) {
        sounds[id] ?: throw SoundException("no sound found for id: $id")
        sounds[id]!!.play()
    }

    /**
     * Stops the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     */
    fun stop(id: String) {
        sounds[id] ?: throw SoundException("no sound found for id: $id")
        sounds[id]!!.stop()
    }

    /**
     * Pauses the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     */
    fun pause(id: String) {
        sounds[id] ?: throw SoundException("no sound found for id: $id")
        sounds[id]!!.pause()
    }

    /**
     * Adjusts the volume of the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     * @param volume the new volume for the Sound
     */
    fun adjustVolume(id: String, volume: Double) {
        sounds[id] ?: throw SoundException("no sound found for id: $id")
        sounds[id]!!.adjustVolume(volume)
    }

    /**
     * Adjusts the volume of all the Sounds tracked by the SoundManager
     *
     * @param volume the new volume for all the Sounds
     */
    fun adjustAllVolume(volume: Double, type: String? = null) {
        sounds.filter {
            type == null || it.value.soundType == type
        }.forEach { (_, sound: Sound) ->
            sound.adjustVolume(volume)
        }
    }

    /**
     * Stops all sounds held by the manager
     */
    fun stopAll() {
        sounds.forEach { (_, sound: Sound) -> sound.stop() }
    }

    /**
     * Pauses all sounds held by the manager
     */
    fun pauseAll() {
        sounds.forEach { (_, sound: Sound) -> sound.pause() }
    }
}
