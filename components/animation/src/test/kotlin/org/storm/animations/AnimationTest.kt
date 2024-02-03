package org.storm.animations

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.storm.animations.sprite.SpriteAnimation
import org.storm.animations.sprite.SpriteSheet
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window
import java.util.concurrent.Executors

class AnimationTest : Application() {

  companion object {
    private const val BUFFER_WAIT_TIME = 10.0

    private fun toMilliSeconds(nanoSeconds: Double): Double {
      return nanoSeconds / 1000000
    }

    private val logger = LoggerFactory.getLogger(AnimationTest::class.java)
  }

  private lateinit var downSpriteAnimation: SpriteAnimation
  private lateinit var rightSpriteAnimation: SpriteAnimation
  private lateinit var upSpriteAnimation: SpriteAnimation
  private lateinit var leftSpriteAnimation: SpriteAnimation

  override fun start(primaryStage: Stage) {
    val window = Window(Resolution(320.0, 240.0))
    val spriteSheet = SpriteSheet("src/test/resources/spriteSheet.png", 32, 32)

    downSpriteAnimation = SpriteAnimation(sprites = spriteSheet.row(0), delay = 8, loops = Animation.LOOP_INDEFINITELY)
    rightSpriteAnimation = SpriteAnimation(sprites = spriteSheet.row(1), delay = 8, loops = 6)
    upSpriteAnimation = SpriteAnimation(sprites = spriteSheet.row(2), delay = 8, loops = 10)
    leftSpriteAnimation = SpriteAnimation(sprites = spriteSheet.row(3), delay = 8, loops = 20)

    val coroutineScope = CoroutineScope(Executors.newSingleThreadExecutor {
      Thread(it).also { thread ->
        thread.isDaemon = true
      }
    }.asCoroutineDispatcher())

    // Loop logic just for testing
    coroutineScope.launch {
      val targetFps = 144.0
      val fixedStepInterval = 1000000000.0 / targetFps // 1/fps in nanoSeconds
      var time = System.nanoTime().toDouble()
      var accumulator = 0.0
      while (true) {
        val currentTime = System.nanoTime().toDouble()
        val elapsedFrameTime = currentTime - time
        time = currentTime
        accumulator += elapsedFrameTime
        while (accumulator >= fixedStepInterval) {
          updateAnimations()
          render(window)
          accumulator -= fixedStepInterval
          time += fixedStepInterval
        }
        var waitTime = toMilliSeconds(time - System.nanoTime() + fixedStepInterval)
        if (waitTime < 0) waitTime = BUFFER_WAIT_TIME // we took too long to execute the loop, so wait for a fixed BUFFER WAIT TIME
        try {
          delay(waitTime.toLong())
        } catch (e: Exception) {
          logger.error("Error occurred during animation loop", e)
        }
      }
    }

    primaryStage.scene = window
    primaryStage.show()
  }

  private fun render(window: Window) {
    window.clear()
    downSpriteAnimation.render(window.graphicsContext, 100.0, 100.0)
    rightSpriteAnimation.render(window.graphicsContext, 100.0, 150.0)
    upSpriteAnimation.render(window.graphicsContext, 150.0, 100.0)
    leftSpriteAnimation.render(window.graphicsContext, 150.0, 150.0)
  }

  private fun updateAnimations() {
    downSpriteAnimation.update(0.0, 0.0)
    rightSpriteAnimation.update(0.0, 0.0)
    upSpriteAnimation.update(0.0, 0.0)
    leftSpriteAnimation.update(0.0, 0.0)
  }

}
