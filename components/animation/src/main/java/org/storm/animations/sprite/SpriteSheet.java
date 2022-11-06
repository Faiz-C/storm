package org.storm.animations.sprite;

import javafx.scene.image.Image;
import lombok.Getter;
import org.storm.core.utils.ImageUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A SpriteSheet holds a collection of sprites derived from a sprite sheet image. The individual cropped sprites are
 * stored in a matrix (2D array) matching the image one for one.
 *
 * Restrictions: the image's width must be divisible by the given spriteWidth and the image's height must be divisible
 *               by the given spriteHeight
 */
public class SpriteSheet {

  private final Image[][] sprites;

  @Getter
  private final int spriteWidth;

  @Getter
  private final int spriteHeight;

  public SpriteSheet(Image spriteSheet, int spriteWidth, int spriteHeight) {
    this.sprites = ImageUtils.toGrid(spriteSheet, spriteWidth, spriteHeight);
    this.spriteWidth = spriteWidth;
    this.spriteHeight = spriteHeight;
  }

  public SpriteSheet(String spriteSheetLocation, int spriteWidth, int spriteHeight) throws FileNotFoundException {
    this(new Image(new FileInputStream(spriteSheetLocation)), spriteWidth, spriteHeight);
  }

  /**
   * @param row row number of the sprite
   * @param col column number of the sprite
   * @return returns the sprite found at the given row and col pair
   */
  public Image getSprite(int row, int col) {
    return this.sprites[row][col];
  }

  /**
   * @param row row number of the wanted sprite row
   * @return returns all sprites in the wanted row
   */
  public Image[] getRow(int row) {
    return this.sprites[row];
  }

  /**
   * @param col column number of the wanted sprite column
   * @return returns all sprites in the wanted column
   */
  public Image[] getCol(int col) {
    Image[] columnSprites = new Image[this.sprites.length];
    for (int r = 0; r < this.sprites.length; r++) {
      columnSprites[r] = this.sprites[r][col];
    }
    return columnSprites;
  }
}
