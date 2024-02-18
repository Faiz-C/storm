package org.storm.animations.sprite

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import org.storm.animations.Animation
import org.storm.animations.serialization.SpriteAnimationDeserializer
import org.storm.core.asset.Asset

/**
 * A specific type of Animation focusing on the use of 2D Sprites and SpriteSheets.
 */
@Asset("animation", "sprite")
@JsonDeserialize(using = SpriteAnimationDeserializer::class)
class SpriteAnimation(
  override val type: String = "animation-sprite",
  override val delay: Int,
  override val loops: Int,
  private val sprites: Array<Image>
): Animation {

  private var currentIteration = 0
  private var currentLoop = 0
  private var currentSprite = 0

  private var hasCompletedOnce = false

  private var playing: Boolean = true

  init {
    require(!(loops != Animation.LOOP_INDEFINITELY && loops < 0)) {
      "loopCount must be >= 0"
    }
  }

  override fun play() {
    this.playing = true
  }

  override fun pause() {
    this.playing = false
  }

  override fun reset() {
    this.currentIteration = 0
    this.currentSprite = 0
    this.currentLoop = 0
  }

  override fun isPlaying(): Boolean {
    return this.playing
  }

  override fun isComplete(): Boolean {
    return (loops == Animation.LOOP_INDEFINITELY && hasCompletedOnce) || currentLoop == loops
  }

  override fun render(gc: GraphicsContext, x: Double, y: Double) {
    gc.drawImage(sprites[currentSprite], x, y)
  }

  override fun update(time: Double, elapsedTime: Double) {
    if (!playing || currentLoop == loops) return

    if (++currentIteration == delay) {
      currentIteration = 0
      currentSprite++
    }

    if (currentSprite == sprites.size) {
      currentSprite = 0
      currentLoop = if (currentLoop == Animation.LOOP_INDEFINITELY) Animation.LOOP_INDEFINITELY else currentLoop + 1
      hasCompletedOnce = true
    }
  }
}
