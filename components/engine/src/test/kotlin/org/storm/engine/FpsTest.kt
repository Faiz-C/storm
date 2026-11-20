package org.storm.engine

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.graphics.Resolution
import org.storm.core.graphics.render.RenderContext
import org.storm.core.input.ActionState
import org.storm.core.input.InputManager
import org.storm.core.input.InputState
import org.storm.core.sound.SoundManager
import org.storm.engine.state.GameState
import org.storm.core.telemetry.TelemetryTracker
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.ImpulseResolutionPhysicsEngine
import org.storm.physics.collision.Collider

class FpsTest: Application() {
    override fun start(primaryStage: Stage) {
        val window = JfxWindow(Resolution.HD)
        val stormEngine = StormEngine(
            renderingEngine = window.renderingEngine,
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 180,
            logicFps = 60,
            inputManager = InputManager(),
            soundManager = SoundManager()
        )

        val state = object: GameState {
            override val colliders: Set<Collider> = setOf()

            override fun getActionState(inputState: InputState): ActionState {
                return ActionState(emptyMap())
            }

            override fun process(actionState: ActionState) {
                // Noop
            }

            override fun render(context: RenderContext, x: Double, y: Double) {
                TelemetryTracker.render(context, x, y)
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