package org.storm.maps.tile

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.render.impl.JfxImage
import org.storm.core.ui.impl.JfxWindow

class TileSetTest : Application() {

    override fun start(primaryStage: Stage) {
        val window = JfxWindow()
        val tileSet = TileSet(JfxImage("src/test/resources/tiles/testTileSet.png"), 32.0, 32.0)
        runBlocking {
            window.canvas.drawImage(tileSet.tile(0), 100.0, 50.0)
            window.canvas.drawImage(tileSet.tile(1), 150.0, 100.0)
            window.canvas.drawImage(tileSet.tile(2), 200.0, 150.0)
        }
        primaryStage.scene = window
        primaryStage.show()
    }

}
