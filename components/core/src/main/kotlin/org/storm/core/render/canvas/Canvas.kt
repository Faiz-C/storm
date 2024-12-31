package org.storm.core.render.canvas

import org.storm.core.extensions.pixels
import org.storm.core.render.Image
import org.storm.core.render.geometry.PixelPoint
import org.storm.core.render.geometry.Point

abstract class Canvas(
    private val defaultSettings: Settings = Settings()
) {
    var settings: Settings = defaultSettings
        private set

    init {
        this.settings = defaultSettings // Trigger the onSettingsChange
    }

    /**
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate to start drawing the text from in game engine units
     * @param y the y coordinate to start drawing the text from in game engine units
     */
    suspend fun drawText(text: String, x: Double, y: Double) {
        drawTextWithPixels(text, x.pixels, y.pixels)
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
        drawPolygonWithPixels(points.map { PixelPoint(it.x.pixels, it.y.pixels) })
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
    suspend fun withSettings(settings: Settings, block: suspend (Canvas) -> Unit) {
        try {
            this.settings = settings
            onSettingsChange(this.settings)
            block(this)
        } finally {
            this.settings = defaultSettings
            onSettingsChange(this.settings)
        }
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
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate to start drawing the text from in pixels
     * @param y the y coordinate to start drawing the text from in pixels
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
    abstract suspend fun drawPolygonWithPixels(points: List<PixelPoint>)

    /**
     * Draws the given Image at the given coordinates. The coordinates represent the top left corner of the Image.
     *
     * @param image the Image to draw
     * @param x x coordinate in pixels of the top left corner
     * @param y y coordinate in pixels of the top left corner
     */
    abstract suspend fun drawImageWithPixels(image: Image, x: Double, y: Double)
}