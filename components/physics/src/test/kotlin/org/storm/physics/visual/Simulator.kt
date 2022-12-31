package org.storm.physics.visual

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.util.Duration
import org.storm.core.ui.Resolution
import org.storm.physics.ImpulseResolutionPhysicsEngine
import org.storm.physics.PhysicsEngine

class Simulator(
  resolution: Resolution,
  private val targetFps: Double,
  private val renderHandler: () -> Unit
) {

  companion object {
    private fun toSeconds(nanoSeconds: Double): Double {
      return nanoSeconds * 0.000000001
    }
  }

  val physicsEngine: PhysicsEngine = ImpulseResolutionPhysicsEngine(resolution)

  private var lastUpdateTime: Double = System.nanoTime().toDouble()
  private val fixedStepInterval: Double = 1000000000.0 / this.targetFps
  private var accumulator = 0.0

  fun simulate() {
    val loopFrame = KeyFrame(Duration.millis(1000.0 / targetFps), { e: ActionEvent -> this.run(e) })
    val timeline = Timeline(targetFps, loopFrame)
    timeline.isAutoReverse = false
    timeline.cycleCount = Animation.INDEFINITE
    timeline.play()
  }

  private fun run(e: Event) {
    val now = System.nanoTime().toDouble()
    val elapsedFrameTime = now - lastUpdateTime
    lastUpdateTime = now
    accumulator += elapsedFrameTime
    while (accumulator >= fixedStepInterval) {
      physicsEngine.update(toSeconds(lastUpdateTime), toSeconds(elapsedFrameTime))
      accumulator -= fixedStepInterval
      lastUpdateTime += fixedStepInterval
    }
    renderHandler()
  }

}
