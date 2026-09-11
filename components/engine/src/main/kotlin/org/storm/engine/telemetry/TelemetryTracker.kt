package org.storm.engine.telemetry

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.update.Updatable

object TelemetryTracker: Updatable, Renderable {

    private var accumulator = 0.0
    private var frameCounter = 0.0

    var samplePeriodSeconds: Double = 0.5

    var snapshot: TelemetrySnapshot = TelemetrySnapshot()
        private set


    override suspend fun update(time: Double, elapsedTime: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

}