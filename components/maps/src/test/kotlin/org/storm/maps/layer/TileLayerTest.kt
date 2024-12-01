package org.storm.maps.layer

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.render.geometry.Point
import org.storm.core.render.impl.JfxImage
import org.storm.core.ui.Resolution
import org.storm.core.ui.JfxWindow
import org.storm.maps.tile.TileSet

class TileLayerTest : Application() {

    override fun start(primaryStage: Stage) {
        val window = JfxWindow()
        val tileSet = TileSet(JfxImage("src/test/resources/tiles/testTileSet.png"), 32.0, 32.0)
        val skeleton = arrayOf(
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2)
        )

        val layer: Layer = TileLayer(false, tileSet, skeleton)
        val renderPoint = Point(0.0, 0.0)

        runBlocking { layer.render(window.canvas, renderPoint.x, renderPoint.y) }

        val shiftAmount = 16.0
        val mapWidth = (tileSet.tileWidth * skeleton[0].size).toDouble()
        val mapHeight = (tileSet.tileHeight * skeleton.size).toDouble()

        window.addKeyPressedHandler { keyEvent: KeyEvent ->
            if (keyEvent.code == KeyCode.LEFT && renderPoint.x > 0) {
                renderPoint.translate(-shiftAmount, 0.0)
            }
            if (keyEvent.code == KeyCode.RIGHT && renderPoint.x + Resolution.SD.width < mapWidth) {
                renderPoint.translate(shiftAmount, 0.0)
            }
            if (keyEvent.code == KeyCode.UP && renderPoint.y > 0) {
                renderPoint.translate(0.0, -shiftAmount)
            }
            if (keyEvent.code == KeyCode.DOWN && renderPoint.y + Resolution.SD.height < mapHeight) {
                renderPoint.translate(0.0, shiftAmount)
            }

            runBlocking {
                window.canvas.clear()
                layer.render(window.canvas, renderPoint.x, renderPoint.y)
            }
        }

        primaryStage.scene = window
        primaryStage.show()
    }

}
