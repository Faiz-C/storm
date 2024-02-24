package org.storm.core.ui

import java.awt.Toolkit

/**
 * Represents a screen resolution
 */
data class Resolution(
    val width: Double,
    val height: Double
) {
    companion object {
        val SD = Resolution(640.0, 480.0)
        val HD = Resolution(1280.0, 720.0)
        val FHD = Resolution(1920.0, 1080.0)
        val QHD = Resolution(2560.0, 1440.0)
        val UHD = Resolution(3840.0, 2160.0)
        val FULL_SCREEN = Resolution(
            Toolkit.getDefaultToolkit().screenSize.getWidth(),
            Toolkit.getDefaultToolkit().screenSize.getHeight()
        )
    }
}
