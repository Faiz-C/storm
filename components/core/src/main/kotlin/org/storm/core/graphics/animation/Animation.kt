package org.storm.core.graphics.animation

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.storm.core.graphics.Renderable
import org.storm.core.serialization.PolymorphismResolver
import org.storm.core.update.Updatable

/**
 * Represents a 2D Animation within the game.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonTypeIdResolver(PolymorphismResolver::class)
@JsonIgnoreProperties("type")
interface Animation : Renderable, Updatable {

    companion object {
        const val LOOP_INDEFINITELY = -1
    }

    /**
     * The amount of cycles to delay before moving the animation forward.
     */
    val delay: Int

    /**
     * The amount of times to loop the animation. Use -1 for indefinite looping.
     */
    val loops: Int

    /**
     * Plays the animation
     */
    fun play()

    /**
     * Pauses the animation
     */
    fun pause()

    /**
     * Resets the animation
     */
    fun reset()

    /**
     * @return True if the Animation is playing, false otherwise
     */
    @JsonIgnore
    fun isPlaying(): Boolean

    /**
     * @return True if the Animation is complete (has finished looping or has finished playing once when looping indefinitely),
     * false otherwise
     */
    @JsonIgnore
    fun isComplete(): Boolean
}
