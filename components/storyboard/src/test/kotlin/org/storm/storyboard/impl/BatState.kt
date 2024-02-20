package org.storm.storyboard.impl

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import org.storm.animations.Animation
import org.storm.animations.sprite.SpriteAnimation
import org.storm.animations.sprite.SpriteSheet
import org.storm.core.asset.Asset
import org.storm.core.input.ActionState
import org.storm.storyboard.StoryBoardState
import org.storm.storyboard.dialogue.Script

@Asset("state", "bat-adventure")
class BatState(
    override val id: String,
    override val neighbours: List<String>,
    override var disabled: Boolean = false,
    private val animationType: String,
    script: Script,
) : StoryBoardState {

    private val scriptPlayer = TextBoxScriptPlayer(script)
    private val animation = SpriteAnimation(8, Animation.LOOP_INDEFINITELY, getSprites())

    private var dx = 0.0
    private var dy = 0.0

    private val movementFrameDelay = 5
    private var currentFrame = 0

    override val next: String? get() = if (scriptPlayer.isChoiceRequired()) {
        scriptPlayer.choice?.let { neighbours.getOrNull(it) }
    } else {
        neighbours.firstOrNull()
    }

    override fun isComplete(): Boolean {
        return animation.isComplete() && scriptPlayer.isComplete()
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        animation.render(gc, 60.0 + dx, 20.0 + dy)
        scriptPlayer.render(gc, x, y)
    }

    override fun update(time: Double, elapsedTime: Double) {
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

    private fun move(direction: String) {
        when (direction) {
            "up" -> {
                dx = 0.0
                dy = -1.0
            }
            "down" -> {
                dx = 0.0
                dy = 1.0
            }
            "left" -> {
                dx = -1.0
                dy = 0.0
            }
            "right" -> {
                dx = 1.0
                dy = 0.0
            }
        }
    }
}
