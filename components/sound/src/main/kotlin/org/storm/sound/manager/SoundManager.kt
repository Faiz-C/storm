package org.storm.sound.manager

import org.storm.sound.Sound
import org.storm.sound.exception.SoundException

/**
 * A SoundManager is a helpful way of tracking and using sounds within a game
 */
class SoundManager(
    private val soundMap: MutableMap<String, Sound> = mutableMapOf()
) {

    /**
     * Adds the given sound to the SoundManager under the given id
     *
     * @param id the id reference for the Sound
     * @param sound Sound to track
     */
    fun add(id: String, sound: Sound) {
        soundMap[id] = sound
    }

    /**
     * Removes the sound associated to the given id reference
     *
     * @param id the id reference for the Sound
     */
    fun remove(id: String) {
        soundMap.remove(id)
    }

    /**
     * Plays the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     */
    fun play(id: String) {
        soundMap[id] ?: throw SoundException("no sound found for id: $id")
        soundMap[id]!!.play()
    }

    /**
     * Stops the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     */
    fun stop(id: String) {
        soundMap[id] ?: throw SoundException("no sound found for id: $id")
        soundMap[id]!!.stop()
    }

    /**
     * Pauses the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     */
    fun pause(id: String) {
        soundMap[id] ?: throw SoundException("no sound found for id: $id")
        soundMap[id]!!.pause()
    }

    /**
     * Adjusts the volume of the Sound associated with the given id
     *
     * @param id the id reference for the Sound
     * @param volume the new volume for the Sound
     */
    fun adjustVolume(id: String, volume: Double) {
        soundMap[id] ?: throw SoundException("no sound found for id: $id")
        soundMap[id]!!.adjustVolume(volume)
    }

    /**
     * Adjusts the volume of all the Sounds tracked by the SoundManager
     *
     * @param volume the new volume for all the Sounds
     */
    fun adjustAllVolume(volume: Double) {
        soundMap.forEach { (_, sound: Sound) -> sound.adjustVolume(volume) }
    }

    /**
     * Stops all sounds held by the manager
     */
    fun stopAll() {
        soundMap.forEach { (_, sound: Sound) -> sound.stop() }
    }

    /**
     * Pauses all sounds held by the manager
     */
    fun pauseAll() {
        soundMap.forEach { (_, sound: Sound) -> sound.pause() }
    }
}
