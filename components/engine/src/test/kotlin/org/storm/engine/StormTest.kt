package org.storm.engine

import javafx.application.Application
import javafx.stage.Stage
import org.storm.engine.example.*

class StormTest : Application() {

  override fun start(primaryStage: Stage) {
    val stormEngine = StormEngine(renderFps = 144, logicFps = 180)

    stormEngine.addState(KeyActionConstants.ONE, AtRestTestState())
    stormEngine.addState(KeyActionConstants.TWO, BouncingBallTestState())
    stormEngine.addState(KeyActionConstants.THREE, ParticleTestState())
    stormEngine.addState(KeyActionConstants.FOUR, CircleCornerTestState())

    stormEngine.addKeyTranslator(TranslatorImpl())
    stormEngine.fpsChangeAllow = false

    stormEngine.swapState(KeyActionConstants.THREE)
    stormEngine.run()

    primaryStage.scene = stormEngine.window
    primaryStage.show()
  }

}
