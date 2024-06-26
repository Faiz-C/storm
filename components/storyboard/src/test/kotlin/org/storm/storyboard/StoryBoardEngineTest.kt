package org.storm.storyboard

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.asset.AssetManager
import org.storm.core.asset.source.types.LocalStorageAssetSource
import org.storm.core.input.ActionEvent
import org.storm.core.input.ActionManager
import org.storm.core.input.ActionTranslator
import org.storm.core.ui.Window
import org.storm.storyboard.helpers.StoryBoardSimulator
import java.nio.file.Paths

class StoryBoardEngineTest : Application() {

    override fun start(stage: Stage) {
        val resourceDir = Paths.get("src", "test", "resources")
        val assetManager = AssetManager()

        assetManager.registerSource(LocalStorageAssetSource(resourceDir.toString()))

        val engine = StoryBoardEngine(assetManager = assetManager, assetSourceId = "local-storage")
        engine.loadScene("bats")
        engine.switchState("top-left")

        val window = Window()

        val actionManager = ActionManager()

        val inputActionActionTranslator = ActionTranslator<KeyEvent> { t ->
            when (t.code) {
                KeyCode.ENTER -> "progress"
                KeyCode.ESCAPE -> "exit"
                KeyCode.UP -> "up"
                KeyCode.DOWN -> "down"
                else -> ""
            }
        }

        window.addKeyPressedHandler {
            runBlocking { actionManager.submitActionEvent(ActionEvent(inputActionActionTranslator.translate(it), true)) }
        }

        window.addKeyReleasedHandler {
            runBlocking { actionManager.submitActionEvent(ActionEvent(inputActionActionTranslator.translate(it), false)) }
        }

        val simulator = StoryBoardSimulator(144.0, {
            window.clear()
            engine.render(window.graphicsContext, 0.0, 0.0)
        }) { time, elapsedTime ->
            engine.process(actionManager.getStateSnapshot())
            engine.update(time, elapsedTime)
        }

        simulator.simulate()

        stage.scene = window
        stage.show()
    }

}
