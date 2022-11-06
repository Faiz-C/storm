package org.storm.core.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * A collection of helpful functions dealing with Images
 */
public class ImageUtils {

  private ImageUtils() {}

  /**
   * @param image Image to crop
   * @param x x coordinate on the image
   * @param y y coordinate on the image
   * @param w width of rectangle to crop
   * @param h height of rectangle to crop
   * @return a rectangular crop of the given image
   */
  public static Image crop(Image image, int x, int y, int w, int h) {
    return new WritableImage(image.getPixelReader(), x, y, w, h);
  }

  /**
   * @param image Image to convert
   * @param width width of the rectangles in the grid
   * @param height height of the rectangles in the grid
   * @return a two dimensional array representing the image as a grid
   */
  public static Image[][] toGrid(Image image, int width, int height) {
    Image[][] grid = new Image[(int)(image.getHeight() / height)][(int)(image.getWidth() / width)];
    for (int r = 0; r < grid.length; r++) {
      for (int c = 0; c < grid[0].length; c++) {
        grid[r][c] = ImageUtils.crop(image, c * width, r * height, width, height);
      }
    }
    return grid;
  }

}
