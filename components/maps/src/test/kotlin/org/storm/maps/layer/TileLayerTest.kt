package org.storm.maps.layer

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.input.InputBindings
import org.storm.core.input.action.ActionEvent
import org.storm.core.render.geometry.Point
import org.storm.core.render.impl.JfxImage
import org.storm.core.ui.Resolution
import org.storm.core.ui.impl.JfxWindow
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

        val shiftAmount = 16.0
        val mapWidth = (tileSet.tileWidth * skeleton[0].size).toDouble()
        val mapHeight = (tileSet.tileHeight * skeleton.size).toDouble()

        runBlocking {
            layer.render(window.canvas, renderPoint.x, renderPoint.y)

            EventManager.getEventStream<KeyEvent>(JfxWindow.KEY_EVENT_STREAM).addConsumer {
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> {
                        if (it.code == KeyCode.LEFT && renderPoint.x > 0) {
                            renderPoint.translate(-shiftAmount, 0.0)
                        }
                        if (it.code == KeyCode.RIGHT && renderPoint.x + Resolution.SD.width < mapWidth) {
                            renderPoint.translate(shiftAmount, 0.0)
                        }
                        if (it.code == KeyCode.UP && renderPoint.y > 0) {
                            renderPoint.translate(0.0, -shiftAmount)
                        }
                        if (it.code == KeyCode.DOWN && renderPoint.y + Resolution.SD.height < mapHeight) {
                            renderPoint.translate(0.0, shiftAmount)
                        }
                    }
                }

                window.canvas.clear()
                layer.render(window.canvas, renderPoint.x, renderPoint.y)
            }

        }

        primaryStage.scene = window
        primaryStage.show()
    }

}
