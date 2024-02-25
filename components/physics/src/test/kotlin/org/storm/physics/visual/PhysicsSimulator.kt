package org.storm.physics.visual

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import org.storm.core.ui.Resolution
import org.storm.core.utils.scheduleOnInterval
import org.storm.physics.ImpulseResolutionPhysicsEngine
import org.storm.physics.PhysicsEngine
import java.util.concurrent.Executors

class PhysicsSimulator(
    resolution: Resolution,
    private val targetFps: Double,
    private val renderHandler: suspend () -> Unit
) {

    companion object {
        private fun toSeconds(nanoSeconds: Double): Double {
            return nanoSeconds * 0.000000001
        }
    }

    private val coroutineScope = CoroutineScope(Executors.newSingleThreadExecutor() {
        Thread(it).apply { isDaemon = true }
    }.asCoroutineDispatcher())

    val physicsEngine: PhysicsEngine = ImpulseResolutionPhysicsEngine(resolution)

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
            physicsEngine.update(toSeconds(lastUpdateTime), toSeconds(elapsedFrameTime))
            accumulator -= fixedStepInterval
            lastUpdateTime += fixedStepInterval
        }

        withContext(Dispatchers.JavaFx) {
            renderHandler()
        }
    }

}
