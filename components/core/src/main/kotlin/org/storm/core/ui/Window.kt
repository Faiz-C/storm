package org.storm.core.ui

import org.storm.core.render.canvas.Canvas

/**
 * A Window is a UI element where the game contents are displayed on. It supports standard or custom Resolutions and can
 * be drawn on using it's Canvas.
 */
interface Window {
    var resolution: Resolution
    val canvas: Canvas
}