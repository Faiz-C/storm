package org.storm.maps.tile

import javafx.scene.image.Image
import org.storm.core.utils.ImageUtils.toGrid
import java.io.FileInputStream

class TileSet(
  tileSetImage: Image,
  val tileWidth: Int,
  val tileHeight: Int
) {

  private val tiles: Array<Array<Image>> = toGrid(tileSetImage, tileWidth, tileHeight)

  val columnCount: Int = tiles[0].size

  val rowCount: Int = tiles.size

  constructor(
    tileSetImagePath: String,
    tileWidth: Int,
    tileHeight: Int
  ) : this(Image(FileInputStream(tileSetImagePath)), tileWidth, tileHeight)

  fun tile(tileNum: Int): Image = tiles[tileNum / columnCount][tileNum % columnCount]
}
