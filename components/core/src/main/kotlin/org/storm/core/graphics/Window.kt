package org.storm.core.graphics

import org.storm.core.graphics.canvas.Canvas

/**
 * A Window is a UI element where the game contents are displayed on. It supports standard or custom Resolutions and can
 * be drawn on using it's Canvas.
 */
interface Window {
    val resolution: Resolution
    val canvas: Canvas
}