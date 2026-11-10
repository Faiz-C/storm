package org.storm.core.graphics.render

import org.storm.core.graphics.Image
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import org.storm.core.graphics.render.brush.Color
import org.storm.core.graphics.render.brush.Font

/**
 * High level API for render requests for the current frame. Game entities use this render content to the screen.
 */
abstract class RenderContext {

    /**
     * The current settings of the RenderContext
     */
    var settings: Settings = Settings()

    protected val settingHistory: ArrayDeque<Settings> = ArrayDeque()

    /**
     * Executes the given block of code using the given brush settings.
     *
     * @param settings the Brush settings to use
     * @param block the block of code to run
     */
    fun withSettings(settings: Settings, block: RenderContext.() -> Unit) {
        try {
            this.settingHistory.add(this.settings)
            this.settings = settings
            onSettingsChange(this.settings)
            block()
        } finally {
            this.settings = this.settingHistory.removeLast()
            onSettingsChange(this.settings)
        }
    }

    /**
     * Executes the given block of code using the given brush settings.
     *
     * @param thickness the thickness of the brush stroke
     * @param color the color of the brush
     * @param fill whether to fill shapes (true) or just draw outlines (false)
     * @param font the font to use for text
     * @param alpha the global alpha value of the canvas
     * @param block the block of code to run with these settings
     */
    fun withSettings(
        thickness: Double = settings.thickness,
        color: Color = settings.color,
        fill: Boolean = settings.fill,
        font: Font = settings.font,
        alpha: Double = settings.alpha,
        block: RenderContext.() -> Unit
    ) {
        withSettings(Settings(thickness, color, fill, font, alpha), block)
    }

    /**
     * Triggered whenever settings are adjusted to allow for an implementation to take needed actions.
     *
     * @param settings the new Settings
     */
    abstract fun onSettingsChange(settings: Settings)

    /**
     * Creates a clipping area based on the given boundary such that anything outside it does not
     * get rendered
     *
     * @param boundary The clipping boundary
     * @param block The canvas rendering code to execute within this boundary
     */
    abstract fun withClip(boundary: Rectangle, block: RenderContext.() -> Unit)

    /**
     * Draws a line from (x1, y1) to (x2, y2)
     *
     * @param x1 x coordinate of the starting point in pixels
     * @param y1 y coordinate of the starting point in pixels
     * @param x2 x coordinate of the ending point in pixels
     * @param y2 y coordinate of the ending point in pixels
     */
    abstract fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double)

    /**
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate of the top left corner to start drawing the text from in pixels
     * @param y the y coordinate of the top left corner to start drawing the text from in pixels
     */
    abstract fun drawText(text: String, x: Double, y: Double)

    /**
     * Draws a rectangle with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the top left corner in pixels
     * @param y y coordinate of the top left corner in pixels
     * @param width width of the rectangle in pixels
     * @param height height of the rectangle in pixels
     */
    abstract fun drawRect(x: Double, y: Double, width: Double, height: Double)

    /**
     * Draws an ellipse with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the center in pixels
     * @param y x coordinate of the center in pixels
     * @param width width of the ellipse in pixels
     * @param height height of the ellipse in pixels
     */
    abstract fun drawEllipse(x: Double, y: Double, width: Double, height: Double)

    /**
     * Draws a polygon from the given list of points. The points are given in *clockwise* order.
     *
     * @param points the points of the polygon in *clockwise* order and in pixels
     */
    abstract fun drawPolygon(points: List<Point>)

    /**
     * Draws the given Image at the given coordinates. The coordinates represent the top left corner of the Image.
     *
     * @param image the Image to draw
     * @param x x coordinate in pixels of the top left corner
     * @param y y coordinate in pixels of the top left corner
     */
    abstract fun drawImage(image: Image, x: Double, y: Double)

}