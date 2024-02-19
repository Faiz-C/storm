package org.storm.animations

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.storm.core.asset.Asset
import org.storm.core.asset.serialization.AssetResolver
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable

/**
 * Represents a 2D Animation within the game.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(AssetResolver::class)
@Asset("animation")
interface Animation : Renderable, Updatable {

  companion object {
    const val LOOP_INDEFINITELY = -1
  }

  /**
   * The type of Animation.
   */
  val type: String
    get() {
      val assetAnnotation = this::class.java.getAnnotation(Asset::class.java)
      return "${assetAnnotation.type}-${assetAnnotation.impl}"
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
