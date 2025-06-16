package org.storm.storyboard.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.storm.core.extensions.scheduleOnInterval
import org.storm.core.utils.toSeconds
import java.util.concurrent.Executors

class StoryBoardSimulator(
    private val targetFps: Double,
    private val renderHandler: suspend () -> Unit,
    private val updateHandler: suspend (Double, Double) -> Unit
) {

    private val coroutineScope = CoroutineScope(Executors.newSingleThreadExecutor() {
        Thread(it).apply { isDaemon = true }
    }.asCoroutineDispatcher())

    private var lastUpdateTime: Long = System.nanoTime()
    private val fixedStepInterval: Long = (1000000000.0 / this.targetFps).toLong()
    private var accumulator = 0.0

    fun simulate() {
        coroutineScope.scheduleOnInterval((1000.0 / targetFps).toLong()) { this.run() }
    }

    private suspend fun run() {
        val now = System.nanoTime()
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

