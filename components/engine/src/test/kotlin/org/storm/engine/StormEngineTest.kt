package org.storm.engine

import javafx.application.Application
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.asset.AssetManager
import org.storm.core.asset.source.types.LocalStorageAssetSource
import org.storm.core.event.EventManager
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.core.ui.impl.JfxWindow
import org.storm.engine.example.*
import org.storm.physics.ImpulseResolutionPhysicsEngine
import java.nio.file.Paths

class StormEngineTest : Application() {

    override fun start(primaryStage: Stage) {
        val actionManager = ActionManager()
        val stormEngine = StormEngine(
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 144,
            physicsFps = 240,
            actionManager = actionManager
        )

        val assetManager = AssetManager().also {
            val resourceDir = Paths.get("src", "test", "resources")

            it.registerSource(
                LocalStorageAssetSource(resourceDir.toString())
            )
        }

        stormEngine.addState(KeyActionConstants.ONE, AtRestTestState(assetManager))
        stormEngine.addState(KeyActionConstants.TWO, BouncingBallTestState(assetManager))
        stormEngine.addState(KeyActionConstants.THREE, ParticleTestState(assetManager))
        stormEngine.addState(KeyActionConstants.FOUR, CircleCornerTestState(assetManager))

        val inputBindings = InputBindingsImpl()

        runBlocking {
            EventManager.getEventStream<KeyEvent>(JfxWindow.KEY_EVENT_STREAM).addConsumer {
                val action = inputBindings.getAction(it) ?: return@addConsumer
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> actionManager.submitActionEvent(ActionEvent(action, true))
                    KeyEvent.KEY_RELEASED -> actionManager.submitActionEvent(ActionEvent(action, false))
                }
            }
        }

        stormEngine.fpsChangeAllow = false

        stormEngine.swapState(KeyActionConstants.THREE)
        stormEngine.run()

        primaryStage.scene = stormEngine.window
        primaryStage.show()
    }

}
