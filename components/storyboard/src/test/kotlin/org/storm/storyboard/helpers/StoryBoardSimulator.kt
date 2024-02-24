package org.storm.storyboard.helpers

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

class StoryBoardSimulator(
    private val targetFps: Double,
    private val renderHandler: () -> Unit,
    private val updateHandler: (Double, Double) -> Unit
) {

    companion object {
        private fun toSeconds(nanoSeconds: Double): Double {
            return nanoSeconds * 0.000000001
        }
    }

    private var lastUpdateTime: Double = System.nanoTime().toDouble()
    private val fixedStepInterval: Double = 1000000000.0 / this.targetFps
    private var accumulator = 0.0

    fun simulate() {
        val loopFrame = KeyFrame(Duration.millis(1000.0 / targetFps), { _ -> this.run() })
        val timeline = Timeline(targetFps, loopFrame)
        timeline.isAutoReverse = false
        timeline.cycleCount = Animation.INDEFINITE
        timeline.play()
    }

    private fun run() {
        val now = System.nanoTime().toDouble()
        val elapsedFrameTime = now - lastUpdateTime
        lastUpdateTime = now
        accumulator += elapsedFrameTime
        while (accumulator >= fixedStepInterval) {
            accumulator -= fixedStepInterval
            lastUpdateTime += fixedStepInterval
            updateHandler(toSeconds(now), toSeconds(elapsedFrameTime))
        }
        renderHandler()
    }

}

