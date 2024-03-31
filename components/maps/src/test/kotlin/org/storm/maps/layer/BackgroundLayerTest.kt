package org.storm.maps.layer

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.setResolution
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window

class BackgroundLayerTest : Application() {
    override fun start(primaryStage: Stage) {
        Context.setResolution(Resolution.HD)
        val window = Window()
        val backgroundLayer = BackgroundLayer("src/test/resources/background/testBackground.png")

        runBlocking { backgroundLayer.render(window.graphicsContext, 300.0, 200.0) }

        primaryStage.scene = window
        primaryStage.show()
    }

}
