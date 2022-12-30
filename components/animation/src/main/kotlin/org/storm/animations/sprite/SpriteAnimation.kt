package org.storm.animations.sprite

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import org.storm.animations.Animation

/**
 * A specific type of Animation focusing on the use of 2D Sprites and SpriteSheets.
 */
class SpriteAnimation(
  private val sprites: Array<Image>,
  private val frameDelay: Int,
  private val loopCount: Int
) : Animation {

  private var currentFrame = 0
  private var currentLoop = 0
  private var currentSprite = 0

  private var playing: Boolean = true

  init {
    require(!(loopCount != Animation.LOOP_INDEFINITELY && loopCount < 0)) {
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
    this.currentFrame = 0
    this.currentSprite = 0
    this.currentLoop = 0
  }

  override fun isPlaying(): Boolean {
    return this.playing
  }

  override fun render(gc: GraphicsContext, x: Double, y: Double) {
    gc.drawImage(sprites[currentSprite], x, y)
  }

  override fun update(time: Double, elapsedTime: Double) {
    if (!playing || currentLoop == loopCount) return

    currentFrame++

    if (currentFrame == frameDelay) {
      currentFrame = 0
      currentSprite++
    }

    if (currentSprite == sprites.size) {
      currentSprite = 0
      currentLoop = if (currentLoop == Animation.LOOP_INDEFINITELY) Animation.LOOP_INDEFINITELY else currentLoop + 1
    }
  }
}
