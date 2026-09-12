package org.storm.engine.telemetry

data class TelemetrySnapshot(
    val fps: Int = 0,
    val frameTimeMs: Double = 0.0,
    val inputProcessingTimeMs: Double = 0.0,
    val updateTimeMs: Double = 0.0,
    val physicsTimeMs: Double = 0.0,
    val renderTimeMs: Double = 0.0
)