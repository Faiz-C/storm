package org.storm.engine

import javafx.application.Application
import javafx.event.Event
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.core.sound.SoundManager
import org.storm.engine.example.*
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.ImpulseResolutionPhysicsEngine

class StormEngineTest : Application() {

    override fun start(primaryStage: Stage) {
        val window = JfxWindow()
        val actionManager = ActionManager()
        val stormEngine = StormEngine(
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 144,
            physicsFps = 240,
            actionManager = actionManager,
            soundManager = SoundManager(),
            window = window,
            renderingDispatcher = Dispatchers.JavaFx // For JavaFx we need to render on its UI dispatcher coroutines
        )

        EventManager.registerJfxKeyEvents(window)

        val inputBindings = InputBindingsImpl()

        runBlocking {
            stormEngine.registerState(KeyActionConstants.ONE, AtRestTestState())
            stormEngine.registerState(KeyActionConstants.TWO, BouncingBallTestState())
            stormEngine.registerState(KeyActionConstants.THREE, ParticleTestState())
            stormEngine.registerState(KeyActionConstants.FOUR, CircleCornerTestState())

            EventManager.getJfxKeyEventStream().addConsumer {
                val action = inputBindings.getAction(it) ?: return@addConsumer
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> actionManager.submitActionEvent(ActionEvent(action, true, it))
                    KeyEvent.KEY_RELEASED -> actionManager.submitActionEvent(ActionEvent(action, false, it))
                }
            }
            stormEngine.fpsChangeAllow = false
            stormEngine.swapState(KeyActionConstants.THREE)
            stormEngine.run()
        }


        primaryStage.scene = window
        primaryStage.show()
    }

}
