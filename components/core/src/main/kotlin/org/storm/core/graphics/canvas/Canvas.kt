package org.storm.core.graphics.canvas

import org.storm.core.extensions.pixels
import org.storm.core.graphics.Image
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle
import org.storm.core.graphics.render.RenderCommand
import org.storm.core.graphics.render.RenderingBuffer

/**
 * A high level API to render content to the screen. This is what game entities should use to render content. Internally,
 * this acts a recorder in the render pipeline, tracking what should be done into a [RenderingBuffer]. The buffer is
 * then utilized by specific rendering implementations.
 */
class Canvas(defaultSettings: Settings = Settings()) {

    /**
     * The current brush settings of the Canvas
     */
    var settings: Settings = defaultSettings
        private set(value) {
            field = value
            this.buffer.add(RenderCommand.ChangeSettings(field))
        }

    /**
     * The buffer where [RenderCommand]s will be written to.
     */
    internal var buffer: RenderingBuffer = RenderingBuffer()

    private val settingHistory: ArrayDeque<Settings> = ArrayDeque()

    /**
     * Draws a line from (x1, y1) to (x2, y2)
     *
     * @param x1 x coordinate of the starting point in pixels
     * @param y1 y coordinate of the starting point in pixels
     * @param x2 x coordinate of the ending point in pixels
     * @param y2 y coordinate of the ending point in pixels
     */
    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        this.buffer.add(RenderCommand.Line(x1, y1, x2, y2))
    }

    /**
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate of the top left corner to start drawing the text from in pixels
     * @param y the y coordinate of the top left corner to start drawing the text from in pixels
     */
    fun drawText(text: String, x: Double, y: Double) {
        this.buffer.add(RenderCommand.Text(text, x, y))
    }

    /**
     * Draws a rectangle with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the top left corner in pixels
     * @param y y coordinate of the top left corner in pixels
     * @param width width of the rectangle in pixels
     * @param height height of the rectangle in pixels
     */
    fun drawRect(x: Double, y: Double, width: Double, height: Double) {
        this.buffer.add(RenderCommand.Rect(x, y, width, height))
    }

    /**
     * Draws an ellipse with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the center in pixels
     * @param y x coordinate of the center in pixels
     * @param width width of the ellipse in pixels
     * @param height height of the ellipse in pixels
     */
    fun drawEllipse(x: Double, y: Double, width: Double, height: Double) {
        this.buffer.add(RenderCommand.Ellipse(x, y, width, height))
    }

    /**
     * Draws a polygon from the given list of points. The points are given in *clockwise* order.
     *
     * @param points the points of the polygon in *clockwise* order and in pixels
     */
    fun drawPolygon(points: List<Point>) {
        this.buffer.add(RenderCommand.Polygon(points))
    }

    /**
     * Draws the given Image at the given coordinates. The coordinates represent the top left corner of the Image.
     *
     * @param image the Image to draw
     * @param x x coordinate in pixels of the top left corner
     * @param y y coordinate in pixels of the top left corner
     */
    fun drawImage(image: Image, x: Double, y: Double) {
        this.buffer.add(RenderCommand.Image(image, x, y))
    }

    /**
     * Draws the given text onto the screen starting at the given coordinates.
     *
     * @param text the text to draw onto the screen
     * @param x the x coordinate *of the bottom left corner* to start drawing the text from in game units
     * @param y the y coordinate *of the bottom left corner* to start drawing the text from in game units
     */
    fun drawTextWithUnits(text: String, x: Double, y: Double) {
        drawText(text, x.pixels, y.pixels)
    }

    /**
     * Draws a line from (x1, y1) to (x2, y2)
     *
     * @param x1 x coordinate of the starting point in game engine units
     * @param y1 y coordinate of the starting point in game engine units
     * @param x2 x coordinate of the ending point in game engine units
     * @param y2 y coordinate of the ending point in game engine units
     */
    fun drawLineWithUnits(x1: Double, y1: Double, x2: Double, y2: Double) {
        drawLine(x1.pixels, y1.pixels, x2.pixels, y2.pixels)
    }

    /**
     * Draws an ellipse with the given x and y coordinates with the given width and height.
     *
     * @param x x coordinate of the center in game engine units
     * @param y y coordinate of the center in game engine units
     * @param width width of the ellipse in game engine units
     * @param height height of the ellipse in game engine units
     */
    fun drawEllipseWithUnits(x: Double, y: Double, width: Double, height: Double) {
        drawEllipse(
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
    fun drawRectWithUnits(x: Double, y: Double, width: Double, height: Double) {
        drawRect(
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
    fun drawPolygonWithUnits(points: List<Point>) {
        drawPolygon(points.map { it.toPixels() })
    }

    /**
     * Draws the given Image at the given coordinates. The coordinates represent the top left corner of the Image.
     *
     * @param image the Image to draw
     * @param x x coordinate in game engine units of the top left corner
     * @param y y coordinate in game engine units of the top left corner
     */
    fun drawImageWithUnits(image: Image, x: Double, y: Double) {
        drawImage(image, x.pixels, y.pixels)
    }

    /**
     * Creates a clipping area based on the given boundary such that anything outside it does not
     * get rendered
     *
     * @param boundary The clipping boundary
     * @param block The canvas rendering code to execute within this boundary
     */
    fun withClip(boundary: Rectangle, block: Canvas.() -> Unit) {
        this.buffer.add(RenderCommand.Clip(boundary))
        block()
        this.buffer.add(RenderCommand.Unclip)
    }

    /**
     * Executes the given block of code using the given brush settings.
     *
     * @param settings the Brush settings to use
     * @param block the block of code to run
     */
    fun withSettings(settings: Settings, block: Canvas.() -> Unit) {
        try {
            this.settingHistory.add(this.settings)
            this.settings = settings
            // onSettingsChange(this.settings) this moves to the renderer
            block()
        } finally {
            this.settings = this.settingHistory.removeLast()
            //onSettingsChange(this.settings)
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
        block: Canvas.() -> Unit
    ) {
        withSettings(Settings(thickness, color, fill, font, alpha), block)
    }

    /**
     * Clears the internal buffer. This method is primarily available to allow for usage of this object within a
     * different pipeline definition.
     */
    fun clear() {
        this.buffer.clear()
    }

}