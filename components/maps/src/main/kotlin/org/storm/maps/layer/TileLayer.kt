package org.storm.maps.layer

import javafx.scene.canvas.GraphicsContext
import org.storm.core.ui.Resolution
import org.storm.maps.tile.Tile
import org.storm.maps.tile.TileSet
import org.storm.physics.math.geometry.shapes.AABB

class TileLayer(
  active: Boolean,
  resolution: Resolution,
  private val tileSet: TileSet,
  private val skeleton: Array<IntArray>
) : Layer(active, resolution) {

  companion object {
    private fun Array<IntArray>.doubleForEach(consumer: (Int, Int) -> Unit) {
      for (r in this.indices) {
        for (c in this[0].indices) {
          consumer(r, c)
        }
      }
    }
  }

  init {
    if (active) {
      loadTiles()
    }
  }

  override fun render(gc: GraphicsContext, x: Double, y: Double) {
    val screenRect = AABB(x, y, resolution.width, resolution.height)

    skeleton.doubleForEach { r, c ->
      if (skeleton[r][c] < 0) return@doubleForEach

      val tileImage = tileSet.tile(skeleton[r][c])
      val tx = c * tileImage.width
      val ty = r * tileImage.height

      val tileRect = AABB(tx, ty, tileImage.width, tileImage.height)

      // Only draw if its visible on the screen
      if (tileRect.intersects(screenRect)) {
        gc.drawImage(tileImage, tx - x, ty - y)
      }
    }
  }

  override fun update(time: Double, elapsedTime: Double) {
    // Tile Layers do not update by default
  }

  private fun loadTiles() {
    skeleton.doubleForEach { r, c ->
      if (skeleton[r][c] < 0) return@doubleForEach

      addEntity(Tile(
        c * tileSet.tileWidth.toDouble(), r * tileSet.tileHeight.toDouble(),
        tileSet.tileWidth.toDouble(), tileSet.tileHeight.toDouble()
      ))
    }
  }
}
