package org.storm.sound.manager

import com.fasterxml.jackson.core.type.TypeReference
import org.storm.core.asset.AssetManager
import org.storm.core.context.Context
import org.storm.core.utils.observation.Observable
import org.storm.core.utils.observation.Observer
import org.storm.sound.Sound
import org.storm.sound.context.BGM_VOLUME
import org.storm.sound.context.EFFECT_VOLUME
import org.storm.sound.context.MASTER_VOLUME
import org.storm.sound.context.VOICE_VOLUME
import org.storm.sound.exception.SoundException

/**
 * A SoundManager is a helpful way of tracking and using sounds within a game
 */
class SoundManager(
    private val assetSourceId: String = "local-storage",
    private val assetManager: AssetManager,
    private val sounds: MutableMap<String, Sound> = mutableMapOf(),
): Observer {

    fun loadSound(id: String) {
        val sound = assetManager.getAsset("sound", id, assetSourceId, object: TypeReference<Sound>() {})
        add(id, sound)
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

    override fun update(o: Observable) {
        if (o !is Context) return

        adjustAllVolume(o.BGM_VOLUME * o.MASTER_VOLUME, Sound.Type.BGM.value)
        adjustAllVolume(o.EFFECT_VOLUME * o.MASTER_VOLUME, Sound.Type.EFFECT.value)
        adjustAllVolume(o.VOICE_VOLUME * o.MASTER_VOLUME, Sound.Type.VOICE.value)
    }
}
