package org.storm.core.utils

import javafx.scene.image.Image
import javafx.scene.image.WritableImage

/**
 * A collection of helpful functions dealing with Images
 */
object ImageUtils {
    /**
     * @param image Image to crop
     * @param x x coordinate on the image
     * @param y y coordinate on the image
     * @param w width of rectangle to crop
     * @param h height of rectangle to crop
     * @return a rectangular crop of the given image
     */
    @JvmStatic
    fun crop(image: Image, x: Int, y: Int, w: Int, h: Int): Image {
        return WritableImage(image.pixelReader, x, y, w, h)
    }

    /**
     * @param image Image to convert
     * @param width width of the rectangles in the grid
     * @param height height of the rectangles in the grid
     * @return a two-dimensional array representing the image as a grid
     */
    @JvmStatic
    fun toGrid(image: Image, width: Int, height: Int): Array<Array<Image>> {
        return Array((image.height / height).toInt()) { r ->
            val colCount = (image.width / width).toInt()
            (0 until colCount).map { c ->
                crop(image, c * width, r * height, width, height)
            }.toTypedArray()
        }
    }
}
