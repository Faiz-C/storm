package org.storm.maps.tile

import org.storm.core.render.Image

class TileSet(
    tileSetImage: Image,
    val tileWidth: Double,
    val tileHeight: Double
) {

    private val tiles: Array<Array<Image>> = tileSetImage.chop(tileWidth, tileHeight)

    val columnCount: Int = tiles[0].size

    val rowCount: Int = tiles.size

    fun tile(tileNum: Int): Image = tiles[tileNum / columnCount][tileNum % columnCount]
}
