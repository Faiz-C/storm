package org.storm.storyboard.impl

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import org.storm.animations.Animation
import org.storm.animations.sprite.SpriteAnimation
import org.storm.animations.sprite.SpriteSheet
import org.storm.core.asset.Asset
import org.storm.core.input.ActionState
import org.storm.core.ui.Resolution
import org.storm.storyboard.StoryBoardState
import org.storm.storyboard.dialogue.script.Script
import kotlin.math.abs

@Asset("state", "bat-adventure")
class BatState(
    override val id: String,
    override val neighbours: List<String>,
    override var disabled: Boolean = false,
    private val animationType: String,
    script: Script,
) : StoryBoardState {

    companion object {
        private const val MOVEMENT_SPEED = 4.0 // px
        private const val X_BOUNDARY = 80.0 // px away from each side
        private const val Y_TOP_BOUNDARY = 20.0 // px away from each side
        private const val Y_BOTTOM_BOUNDARY = 250.0 // px away from each side

        private var batX = X_BOUNDARY
        private var batY = Y_TOP_BOUNDARY
    }

    private val scriptPlayer = TextBoxScriptPlayer(script)
    private val animation = SpriteAnimation(8, Animation.LOOP_INDEFINITELY, getSprites())

    private val movementFrameDelay = 5
    private var currentFrame = 0
    private var movementComplete = animationType == "idle"

    override val next: String? get() = if (scriptPlayer.isChoiceRequired()) {
        scriptPlayer.choice?.let { neighbours.getOrNull(it) }
    } else {
        neighbours.firstOrNull()
    }

    override fun isComplete(): Boolean {
        return movementComplete && animation.isComplete() && scriptPlayer.isComplete()
    }

    override fun reset() {
        scriptPlayer.reset()
        animation.reset()
        movementComplete = false
        currentFrame = 0
   }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        animation.render(gc, x + batX, y + batY)
        scriptPlayer.render(gc, x, y)
    }

    override fun update(time: Double, elapsedTime: Double) {
        if (currentFrame++ < movementFrameDelay) {
            updateMovementDeltas(animationType)
            currentFrame = 0
        }

        animation.update(time, elapsedTime)
        scriptPlayer.update(time, elapsedTime)
    }

    override fun process(actionState: ActionState) {
        scriptPlayer.process(actionState)
    }

    private fun getSprites(): Array<Image> {
        val spriteSheet = SpriteSheet("src/test/resources/animation/bat-sprites.png", 32, 32)

        return when (animationType) {
            "idle", "down" -> spriteSheet.row(0)
            "right" -> spriteSheet.row(1)
            "up" -> spriteSheet.row(2)
            "left" -> spriteSheet.row(3)
            else -> throw IllegalArgumentException("Unknown animation type: $animationType")
        }
    }

    private fun updateMovementDeltas(direction: String) {
        // Because this is just for testing we can hardcode this
        val (screenWidth, screenHeight) = Resolution.SD

        when (direction) {
            "up" -> {
                batY = (batY - MOVEMENT_SPEED).coerceAtLeast(Y_TOP_BOUNDARY)
                movementComplete = batY >= Y_TOP_BOUNDARY
            }
            "down" -> {
                batY = (batY + MOVEMENT_SPEED).coerceAtMost(screenHeight - Y_BOTTOM_BOUNDARY)
                movementComplete = batY <= screenHeight - Y_TOP_BOUNDARY
            }
            "left" -> {
                batX = (batX - MOVEMENT_SPEED).coerceAtLeast(X_BOUNDARY)
                movementComplete = batX <= X_BOUNDARY
            }
            "right" -> {
                batX += MOVEMENT_SPEED
                movementComplete = batX >= screenWidth - X_BOUNDARY
            }
            "idle" -> {
                movementComplete = true
            }
        }
    }
}
