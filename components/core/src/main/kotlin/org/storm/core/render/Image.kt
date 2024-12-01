package org.storm.core.render

interface Image {
    val uri: String
    val width: Double
    val height: Double

    /**
     * Crops the image to the given rectangle. All parameter values are in pixels.
     *
     * @param x x coordinate of the top left corner of the rectangle
     * @param y y coordinate of the top left corner of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @return the cropped image
     */
    fun crop(x: Double, y: Double, width: Double, height: Double): Image

    /**
     * Chops the image into a grid of rectangles with the given width and height. All parameter values are in pixels.
     *
     * @param width width of the rectangles in the grid
     * @param height height of the rectangles in the grid
     * @return a two-dimensional array representing the image as a grid
     */
    fun chop(width: Double, height: Double): Array<Array<Image>>
}