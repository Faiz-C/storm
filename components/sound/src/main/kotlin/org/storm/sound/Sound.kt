package org.storm.sound

/**
 * Represents a Sound within the game. Sounds can be played, paused, stopped and have its volume adjusted
 */
interface Sound {

    companion object {
        const val LOOP_INDEFINITELY = -1
    }

    /**
     * The amount of cycles to delay before starting the sound.
     */
    val delay: Int

    /**
     * The amount of times to loop the sound. Use -1 for indefinite looping.
     */
    val loops: Int

    /**
     * Plays the sound
     */
    fun play()

    /**
     * Pauses the sound
     */
    fun pause()

    /**
     * Stops the sound
     */
    fun stop()

    /**
     * Adjusts the volume of the sound to the given volume
     *
     * @param volume a double value in [0. 1] where 1 is 100% and 0 is 0%
     */
    fun adjustVolume(volume: Double)

    /**
     * @return True if the Sound is completed, false otherwise
     */
    fun isComplete(): Boolean
}
