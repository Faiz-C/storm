package org.storm.maps.layer

import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.render.canvas.Canvas
import org.storm.maps.tile.Tile
import org.storm.maps.tile.TileSet
import org.storm.physics.math.geometry.shapes.AABB

class TileLayer(
    active: Boolean,
    private val tileSet: TileSet,
    private val skeleton: Array<IntArray>
) : Layer(active) {

    companion object {
        private suspend fun Array<IntArray>.iterateMatrix(block: suspend (Int, Int) -> Unit) {
            for (r in this.indices) {
                for (c in this[0].indices) {
                    block(r, c)
                }
            }
        }
    }

    init {
        if (active) {
            runBlocking { loadTiles() }
        }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        val resolution = Context.RESOLUTION
        val screenRect = AABB(x, y, resolution.width, resolution.height)

        skeleton.iterateMatrix { r, c ->
            if (skeleton[r][c] < 0) return@iterateMatrix

            val tileImage = tileSet.tile(skeleton[r][c])
            val tx = c * tileImage.width
            val ty = r * tileImage.height

            val tileRect = AABB(tx, ty, tileImage.width, tileImage.height)

            // Only draw if its visible on the screen
            if (tileRect.intersects(screenRect)) {
                canvas.drawImageWithPixels(tileImage, tx - x, ty - y)
            }
        }
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        // Tile Layers do not update by default
    }

    private suspend fun loadTiles() {
        skeleton.iterateMatrix { r, c ->
            if (skeleton[r][c] < 0) return@iterateMatrix

            addEntity(
                Tile(
                    c * tileSet.tileWidth.toDouble(), r * tileSet.tileHeight.toDouble(),
                    tileSet.tileWidth.toDouble(), tileSet.tileHeight.toDouble()
                )
            )
        }
    }
}
