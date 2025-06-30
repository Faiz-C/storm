package org.storm.core.graphics.canvas

import org.storm.core.extensions.pixels
import org.storm.core.graphics.Image
import org.storm.core.graphics.geometry.Point
import java.util.Stack

abstract class Canvas(
    defaultSettings: Settings = Settings()
) {
    var settings: Settings = defaultSettings
        private set

    val settingHistory: Stack<Settings> = Stack()

    /**
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate *of the bottom left corner* to start drawing the text from in game units
     * @param y the y coordinate *of the bottom left corner* to start drawing the text from in game units
     */
    suspend fun drawText(text: String, x: Double, y: Double) {
        drawTextWithPixels(text, x.pixels, y.pixels)
    }

    /**
     * Draws a line from (x1, y1) to (x2, y2)
     *
     * @param x1 x coordinate of the starting point in game engine units
     * @param y1 y coordinate of the starting point in game engine units
     * @param x2 x coordinate of the ending point in game engine units
     * @param y2 y coordinate of the ending point in game engine units
     */
    suspend fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        drawLineWithPixels(x1.pixels, y1.pixels, x2.pixels, y2.pixels)
    }

    /**
     * Draws an ellipse with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the center in game engine units
     * @param y y coordinate of the center in game engine units
     * @param width width of the ellipse in game engine units
     * @param height height of the ellipse in game engine units
     */
    suspend fun drawEllipse(x: Double, y: Double, width: Double, height: Double) {
        drawEllipseWithPixels(
            x.pixels,
            y.pixels,
            width.pixels,
            height.pixels
        )
    }

    /**
     * Draws a rectangle with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the top left corner in game engine units
     * @param y y coordinate of the top left corner in game engine units
     * @param width width of the rectangle in game engine units
     * @param height height of the rectangle in game engine units
     */
    suspend fun drawRect(x: Double, y: Double, width: Double, height: Double) {
        drawRectWithPixels(
            x.pixels,
            y.pixels,
            width.pixels,
            height.pixels
        )
    }

    /**
     * Draws a polygon from the given list of points. The points are given in *clockwise* order.
     *
     * @param points the points of the polygon in *clockwise* order and in game engine units
     */
    suspend fun drawPolygon(points: List<Point>) {
        drawPolygonWithPixels(points.map { it.toPixels() })
    }

    /**
     * Draws the given Image at the given coordinates. The coordinates represent the top left corner of the Image.
     *
     * @param image the Image to draw
     * @param x x coordinate in game engine units of the top left corner
     * @param y y coordinate in game engine units of the top left corner
     */
    suspend fun drawImage(image: Image, x: Double, y: Double) {
        drawImageWithPixels(image, x.pixels, y.pixels)
    }

    /**
     * Executes the given block of code using the given brush settings.
     *
     * @param settings the Brush settings to use
     * @param block the block of code to run
     */
    suspend fun withSettings(settings: Settings, block: suspend Canvas.() -> Unit) {
        try {
            settingHistory.push(this.settings)
            this.settings = settings
            onSettingsChange(this.settings)
            block()
        } finally {
            this.settings = this.settingHistory.pop()
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
     * @param block the block of code to run with these settings
     */
    suspend fun withSettings(
        thickness: Double = settings.thickness,
        color: Color = settings.color,
        fill: Boolean = settings.fill,
        font: Font = settings.font,
        block: suspend Canvas.() -> Unit
    ) {
        withSettings(Settings(thickness, color, fill, font), block)
    }

    /**
     * Clears the Canvas
     */
    abstract suspend fun clear()

    /**
     * A hook which triggers whenever the withBrush method is used to change the active Brush settings. This is where
     * the implementation is able to handle any required logic it needs to use the new settings.
     *
     * @param settings The brush to use
     */
    abstract suspend fun onSettingsChange(settings: Settings)

    /**
     * Draws a line from (x1, y1) to (x2, y2)
     *
     * @param x1 x coordinate of the starting point in pixels
     * @param y1 y coordinate of the starting point in pixels
     * @param x2 x coordinate of the ending point in pixels
     * @param y2 y coordinate of the ending point in pixels
     */
    abstract suspend fun drawLineWithPixels(x1: Double, y1: Double, x2: Double, y2: Double)

    /**
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate of the top left corner to start drawing the text from in pixels
     * @param y the y coordinate of the top left corner to start drawing the text from in pixels
     */
    abstract suspend fun drawTextWithPixels(text: String, x: Double, y: Double)

    /**
     * Draws a rectangle with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the top left corner in pixels
     * @param y y coordinate of the top left corner in pixels
     * @param width width of the rectangle in pixels
     * @param height height of the rectangle in pixels
     */
    abstract suspend fun drawRectWithPixels(x: Double, y: Double, width: Double, height: Double)

    /**
     * Draws an ellipse with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the center in pixels
     * @param y x coordinate of the center in pixels
     * @param width width of the ellipse in pixels
     * @param height height of the ellipse in pixels
     */
    abstract suspend fun drawEllipseWithPixels(x: Double, y: Double, width: Double, height: Double)

    /**
     * Draws a polygon from the given list of points. The points are given in *clockwise* order.
     *
     * @param points the points of the polygon in *clockwise* order and in pixels
     */
    abstract suspend fun drawPolygonWithPixels(points: List<Point>)

    /**
     * Draws the given Image at the given coordinates. The coordinates represent the top left corner of the Image.
     *
     * @param image the Image to draw
     * @param x x coordinate in pixels of the top left corner
     * @param y y coordinate in pixels of the top left corner
     */
    abstract suspend fun drawImageWithPixels(image: Image, x: Double, y: Double)
}