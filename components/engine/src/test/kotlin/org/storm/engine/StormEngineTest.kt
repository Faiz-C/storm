package org.storm.engine

import javafx.application.Application
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.input.InputEvent
import org.storm.core.input.InputManager
import org.storm.core.sound.SoundManager
import org.storm.engine.example.*
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.getJfxMouseEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents
import org.storm.impl.jfx.extensions.registerJfxMouseEvents
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.ImpulseResolutionPhysicsEngine

class StormEngineTest : Application() {

    override fun start(primaryStage: Stage) {
        val window = JfxWindow()
        val inputManager = InputManager(maxInputDeadTimeMillis = 500.0)
        val stormEngine = StormEngine(
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 144,
            physicsFps = 240,
            inputManager = inputManager,
            soundManager = SoundManager(),
            window = window,
            renderingDispatcher = Dispatchers.JavaFx // For JavaFx we need to render on its UI dispatcher coroutines
        )

        EventManager.registerJfxKeyEvents(window)
        EventManager.registerJfxMouseEvents(window)

        runBlocking {
            stormEngine.registerState(Controls.ONE, AtRestTestState())
            stormEngine.registerState(Controls.TWO, BouncingBallTestState())
            stormEngine.registerState(Controls.THREE, ParticleTestState())
            stormEngine.registerState(Controls.FOUR, CircleCornerTestState())

            EventManager.getJfxKeyEventStream().addConsumer {
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> inputManager.processInput(InputEvent(it.code.name, it, true))
                    KeyEvent.KEY_RELEASED -> inputManager.processInput(InputEvent(it.code.name, it, false))
                }
            }

            EventManager.getJfxMouseEventStream().addConsumer {
                when (it.eventType) {
                    MouseEvent.MOUSE_PRESSED -> inputManager.processInput(InputEvent(it.button.name, it))
                    MouseEvent.MOUSE_RELEASED -> inputManager.processInput(InputEvent(it.button.name, it))
                    MouseEvent.MOUSE_MOVED -> inputManager.processInput(InputEvent("mouse-moved", it))
                }
            }

            stormEngine.fpsChangeAllow = false
            stormEngine.swapState(Controls.THREE)
            stormEngine.run()
        }


        primaryStage.scene = window
        primaryStage.show()
    }

}
