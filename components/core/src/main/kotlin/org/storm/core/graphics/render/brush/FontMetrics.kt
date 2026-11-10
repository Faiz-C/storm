package org.storm.core.graphics.render.brush

data class FontMetrics(
    val width: Double,
    val height: Double,
    val visualWidth: Double,
    val visualHeight: Double,
    val visualMinX: Double,
    val visualMinY: Double,
    val ascent: Double,
    val descent: Double
)
