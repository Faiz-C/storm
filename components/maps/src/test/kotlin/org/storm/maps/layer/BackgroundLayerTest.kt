package org.storm.maps.layer

import javafx.application.Application
import javafx.stage.Stage
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window

class BackgroundLayerTest : Application() {
  override fun start(primaryStage: Stage) {
    val window = Window(Resolution.HD)
    val backgroundLayer = BackgroundLayer("src/test/resources/background/testBackground.png", Resolution.HD)
    backgroundLayer.render(window.graphicsContext, 300.0, 200.0)
    primaryStage.scene = window
    primaryStage.show()
  }

}
