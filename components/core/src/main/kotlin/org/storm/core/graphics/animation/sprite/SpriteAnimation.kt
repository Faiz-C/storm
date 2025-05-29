package org.storm.core.graphics.animation.sprite

import org.storm.core.serialization.Polymorphic
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.Image
import org.storm.core.graphics.animation.Animation

/**
 * A specific type of Animation focusing on the use of 2D Sprites and SpriteSheets.
 */
@Polymorphic("sprite-animation")
class SpriteAnimation(
    override val delay: Int,
    override val loops: Int,
    private val sprites: Array<Image>
) : Animation {

    private var currentFrame = 0
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
        playing = true
    }

    override fun pause() {
        playing = false
    }

    override fun reset() {
        currentFrame = 0
        currentSprite = 0
        currentLoop = 0
    }

    override fun isPlaying(): Boolean {
        return playing
    }

    override fun isComplete(): Boolean {
        return (loops == Animation.LOOP_INDEFINITELY && hasCompletedOnce) || currentLoop == loops
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.drawImageWithPixels(sprites[currentSprite], x, y)
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        if (!playing || currentLoop == loops) return

        if (++currentFrame == delay) {
            currentFrame = 0
            currentSprite++
        }

        if (currentSprite == sprites.size) {
            currentSprite = 0
            currentLoop = if (currentLoop == Animation.LOOP_INDEFINITELY) Animation.LOOP_INDEFINITELY else currentLoop + 1
            hasCompletedOnce = true
        }
    }

}
