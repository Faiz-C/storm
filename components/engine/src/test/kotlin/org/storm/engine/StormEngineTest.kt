package org.storm.engine

import javafx.application.Application
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.core.sound.SoundManager
import org.storm.engine.example.*
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.impl.jfx.graphics.getJfxKeyEventStream
import org.storm.physics.ImpulseResolutionPhysicsEngine

class StormEngineTest : Application() {

    override fun start(primaryStage: Stage) {
        val actionManager = ActionManager()
        val stormEngine = StormEngine(
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 144,
            physicsFps = 240,
            actionManager = actionManager,
            soundManager = SoundManager(),
            window = JfxWindow()
        )

        val inputBindings = InputBindingsImpl()

        runBlocking {

            stormEngine.registerState(KeyActionConstants.ONE, AtRestTestState())
            stormEngine.registerState(KeyActionConstants.TWO, BouncingBallTestState())
            stormEngine.registerState(KeyActionConstants.THREE, ParticleTestState())
            stormEngine.registerState(KeyActionConstants.FOUR, CircleCornerTestState())

            EventManager.getJfxKeyEventStream().addConsumer {
                val action = inputBindings.getAction(it) ?: return@addConsumer
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> actionManager.submitActionEvent(ActionEvent(action, true))
                    KeyEvent.KEY_RELEASED -> actionManager.submitActionEvent(ActionEvent(action, false))
                }
            }
            stormEngine.fpsChangeAllow = false
            stormEngine.swapState(KeyActionConstants.THREE)
            stormEngine.run()
        }


        primaryStage.scene = stormEngine.window as JfxWindow
        primaryStage.show()
    }

}
