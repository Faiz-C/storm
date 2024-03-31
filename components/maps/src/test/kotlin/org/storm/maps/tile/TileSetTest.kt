package org.storm.maps.tile

import javafx.application.Application
import javafx.stage.Stage
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window

class TileSetTest : Application() {

    override fun start(primaryStage: Stage) {
        val window = Window()
        val tileSet = TileSet("src/test/resources/tiles/testTileSet.png", 32, 32)
        window.graphicsContext.drawImage(tileSet.tile(0), 100.0, 50.0)
        window.graphicsContext.drawImage(tileSet.tile(1), 150.0, 100.0)
        window.graphicsContext.drawImage(tileSet.tile(2), 200.0, 150.0)
        primaryStage.scene = window
        primaryStage.show()
    }

}
