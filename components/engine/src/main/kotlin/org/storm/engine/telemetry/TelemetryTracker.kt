package org.storm.engine.telemetry

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.canvas.Color
import org.storm.core.update.Updatable

/**
 * A tracker for standard engine telemetry. The data is always updated by the engine but utilizing it is left to the
 * library consumer.
 */
object TelemetryTracker: Updatable, Renderable {

    // A shade of green, slightly translucent
    private val DEBUG_COLOUR = Color(57.0, 150.0, 82.0, 0.6)
    private const val RENDER_MARGIN = 5.0

    private var accumulator = 0.0
    private var frameCounter = 0

    // We use @PublishedApi here because these fields shouldn't be publicly exposed
    // but need to be accessible by inline functions.
    @PublishedApi internal var frameTimeMs = 0.0
    @PublishedApi internal var inputProcessingTimeMs = 0.0
    @PublishedApi internal var updateTimeMs = 0.0
    @PublishedApi internal var physicsTimeMs = 0.0
    @PublishedApi internal var renderTimeMs = 0.0

    /**
     * How many seconds to wait for before capturing a snapshot
     */
    var samplePeriodSeconds: Double = 0.5

    /**
     * How much to smooth for linear interpolation calculations
     */
    var smoothingFactor: Double = 0.15

    /**
     * The current snapshot of telemetry data
     */
    var snapshot: TelemetrySnapshot = TelemetrySnapshot()
        private set

    /**
     * Executes and measures the time taken (with linear interpolation) for the given [block] as input processing time
     *
     * @param block the block of code to execute
     */
    internal suspend inline fun recordInputProcessing(block: suspend () -> Unit) {
        this.inputProcessingTimeMs += measure(this.inputProcessingTimeMs, block)
    }

    /**
     * Executes and measures the time taken (with linear interpolation) for the given [block] as update time
     *
     * @param block the block of code to execute
     */
    internal suspend inline fun recordUpdate(block: suspend () -> Unit) {
        this.updateTimeMs += measure(this.updateTimeMs, block)
    }

    /**
     * Executes and measures the time taken (with linear interpolation) for the given [block] as physics time
     *
     * @param block the block of code to execute
     */
    internal suspend inline fun recordPhysics(block: suspend () -> Unit) {
        this.physicsTimeMs += measure(this.physicsTimeMs, block)
    }

    /**
     * Executes and measures the time taken (with linear interpolation) for the given [block] as render time
     *
     * @param block the block of code to execute
     */
    internal suspend inline fun recordRender(block: suspend () -> Unit) {
        this.renderTimeMs += measure(this.renderTimeMs, block)
    }

    /**
     * Executes and measures the time taken (with linear interpolation) for the given [block]
     *
     * @param block the block of code to execute
     * @return the time taken to execute the block using linear interpolation
     */
    internal suspend inline fun measure(current: Double, block: suspend () -> Unit): Double {
        val start = System.nanoTime()
        block()
        val durationMs = (System.nanoTime() - start) / 1_000_000L

        // Very similar to linear interpolation
        return (durationMs - current) * this.smoothingFactor
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        this.frameCounter++
        this.accumulator += elapsedTime

        this.frameTimeMs += (elapsedTime * 1000.0 - this.frameTimeMs) * this.smoothingFactor

        if (this.accumulator >= this.samplePeriodSeconds) {
            this.snapshot = TelemetrySnapshot(
                fps = (this.frameCounter / this.accumulator).toInt(),
                frameTimeMs = this.frameTimeMs,
                updateTimeMs = this.updateTimeMs,
                renderTimeMs = this.renderTimeMs,
                physicsTimeMs = this.physicsTimeMs,
                inputProcessingTimeMs = this.inputProcessingTimeMs
            )

            this.frameCounter = 0
            this.accumulator = 0.0
        }
    }

    /**
     * Just for debugging purposes. To build a custom view use [snapshot] directly to pull the data needed.
     */
    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        val snapshot = this.snapshot
        canvas.withSettings(color = DEBUG_COLOUR) {
            drawText(
                "FPS: ${snapshot.fps} | FT: ${String.format("%.2f", snapshot.frameTimeMs)}ms",
                RENDER_MARGIN,
                RENDER_MARGIN
            )
        }
    }

}