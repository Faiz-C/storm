package org.storm.animations.sprite

import javafx.scene.image.Image
import org.storm.core.utils.ImageUtils
import java.io.FileInputStream

/**
 * A SpriteSheet holds a collection of sprites derived from a sprite sheet image. The individual cropped sprites are
 * stored in a matrix (2D array) matching the image one for one.
 *
 * Restrictions: the image's width must be divisible by the given spriteWidth and the image's height must be divisible
 * by the given spriteHeight
 */
class SpriteSheet(
  spriteSheet: Image,
  spriteWidth: Int,
  spriteHeight: Int
) {

  private val sprites: Array<Array<Image>> = ImageUtils.toGrid(spriteSheet, spriteWidth, spriteHeight)

  constructor(spriteSheetLocation: String, spriteWidth: Int, spriteHeight: Int) : this(
    Image(FileInputStream(spriteSheetLocation)),
    spriteWidth,
    spriteHeight
  )

  /**
   * @param row row number of the sprite
   * @param col column number of the sprite
   * @return returns the sprite found at the given row and col pair
   */
  fun sprite(row: Int, col: Int): Image = this.sprites[row][col]

  /**
   * @param row row number of the wanted sprite row
   * @return returns all sprites in the wanted row
   */
  fun row(row: Int): Array<Image> = this.sprites[row]

  /**
   * @param col column number of the wanted sprite column
   * @return returns all sprites in the wanted column
   */
  fun col(col: Int): Array<Image> {
    return this.sprites.indices.map {
      this.sprites[it][col]
    }.toTypedArray()
  }
}
