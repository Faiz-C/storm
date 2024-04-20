package org.storm.sound

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.storm.core.asset.Asset
import org.storm.core.asset.serialization.AssetResolver

/**
 * Represents a Sound within the game. Sounds can be played, paused, stopped and have its volume adjusted
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonTypeIdResolver(AssetResolver::class)
interface Sound {

    companion object {
        const val LOOP_INDEFINITELY = -1
    }

    enum class Type(val value: String) {
        BGM("BGM"),
        EFFECT("EFFECT"),
        VOICE("VOICE")
    }

    /**
     * The type of asset, used to help serialize and deserialize the state using Asset loading.
     */
    val type: String
        get() {
            val assetAnnotation = this::class.java.getAnnotation(Asset::class.java)
            return "${assetAnnotation.type}-${assetAnnotation.impl}"
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
     * The type of sound this is. Ex: BGM, EFFECT, VOICE
     */
    val soundType: String

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
