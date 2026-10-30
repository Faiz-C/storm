package org.storm.engine

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.runBlocking
import org.storm.core.graphics.Resolution
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.input.ActionState
import org.storm.core.input.InputManager
import org.storm.core.input.InputState
import org.storm.core.sound.SoundManager
import org.storm.engine.state.GameState
import org.storm.engine.telemetry.TelemetryTracker
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.ImpulseResolutionPhysicsEngine
import org.storm.physics.collision.Collider

class FpsTest: Application() {
    override fun start(primaryStage: Stage) {
        val window = JfxWindow(Resolution.HD)
        val stormEngine = StormEngine(
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 120,
            physicsFps = 60,
            inputManager = InputManager(),
            soundManager = SoundManager(),
            window = window,
            renderingDispatcher = Dispatchers.JavaFx // For JavaFx we need to render on its UI dispatcher coroutines
        )

        val state = object: GameState {
            override val colliders: Set<Collider> = setOf()

            override fun getActionState(inputState: InputState): ActionState {
                return ActionState(emptyMap())
            }

            override suspend fun process(actionState: ActionState) {
                // Noop
            }

            override suspend fun render(canvas: Canvas, x: Double, y: Double) {
                TelemetryTracker.render(canvas, x, y)
            }
        }

        runBlocking {
            stormEngine.swapState(state)
            stormEngine.run()
        }

        primaryStage.scene = window
        primaryStage.show()
    }

}