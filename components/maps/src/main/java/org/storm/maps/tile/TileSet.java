package org.storm.maps.tile;

import javafx.scene.image.Image;
import lombok.Getter;
import org.storm.core.utils.ImageUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TileSet {

  private final Image[][] tiles;

  @Getter
  private final int tileWidth;

  @Getter
  private final int tileHeight;

  @Getter
  private final int columnCount;

  @Getter
  private final int rowCount;

  public TileSet(Image tileSetImage, int tileWidth, int tileHeight) {
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
    this.tiles = ImageUtils.toGrid(tileSetImage, this.tileWidth, this.tileHeight);
    this.columnCount = this.tiles[0].length;
    this.rowCount = this.tiles.length;
  }

  public TileSet(String tileSetImagePath, int tileWidth, int tileHeight) throws FileNotFoundException {
    this(new Image(new FileInputStream(tileSetImagePath)), tileWidth, tileHeight);
  }

  public Image getTileImage(int tileNum) {
    return this.tiles[tileNum / this.columnCount][tileNum % this.columnCount];
  }

}
