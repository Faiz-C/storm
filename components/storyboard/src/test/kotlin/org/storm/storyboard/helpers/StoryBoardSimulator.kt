package org.storm.storyboard.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.storm.core.extensions.scheduleOnInterval
import java.util.concurrent.Executors

class StoryBoardSimulator(
    private val targetFps: Double,
    private val renderHandler: suspend () -> Unit,
    private val updateHandler: suspend (Double, Double) -> Unit
) {

    companion object {
        private fun toSeconds(nanoSeconds: Double): Double {
            return nanoSeconds * 0.000000001
        }
    }

    private val coroutineScope = CoroutineScope(Executors.newSingleThreadExecutor() {
        Thread(it).apply { isDaemon = true }
    }.asCoroutineDispatcher())

    private var lastUpdateTime: Double = System.nanoTime().toDouble()
    private val fixedStepInterval: Double = 1000000000.0 / this.targetFps
    private var accumulator = 0.0

    fun simulate() {
        coroutineScope.scheduleOnInterval((1000.0 / targetFps).toLong()) { this.run() }
    }

    private suspend fun run() {
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

