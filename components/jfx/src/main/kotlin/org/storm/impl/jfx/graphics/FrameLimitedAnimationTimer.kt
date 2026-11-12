package org.storm.impl.jfx.graphics

import javafx.animation.AnimationTimer

/**
 * A simple JFX animation timer that allows for limiting the FPS to values below the maximum hertz of the user's
 * monitor.
 */
class FrameLimitedAnimationTimer(fps: Int, private val render: () -> Unit): AnimationTimer() {

    private val interval = 1_000_000_000 / fps
    private var lastUpdateTime = 0L

    override fun handle(now: Long) {
        // First pulse
        if (this.lastUpdateTime == 0L) {
            this.lastUpdateTime = now
            return
        }

        val elapsedTime = now - this.lastUpdateTime

        // Not enough time has passed, skip this pulse
        if (elapsedTime < this.interval) return

        // set new update time and account for any overshooting that happened with the animation timer.
        this.lastUpdateTime = now - (elapsedTime % interval)

        this.render()
    }

}