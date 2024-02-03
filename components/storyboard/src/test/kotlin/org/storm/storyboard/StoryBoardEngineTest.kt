package org.storm.storyboard

import javafx.application.Application
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.storm.core.asset.AssetManager
import org.storm.core.input.Translator
import org.storm.core.input.action.ActionManager
import org.storm.core.input.action.SimpleActionManager
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window
import org.storm.storyboard.helpers.StoryBoardSimulator
import java.nio.file.Paths

class StoryBoardEngineTest: Application() {

    override fun start(stage: Stage) {
        val resourceDir = Paths.get("components", "storyboard", "src", "test", "resources")
        val engine = StoryBoardEngine(assetManager = AssetManager(assetDir = resourceDir.toString()))

        val window = Window(Resolution.SD)

        val actionManager = SimpleActionManager()

        val inputTranslator = Translator<KeyEvent, String> { t ->
            when (t.code) {
                KeyCode.SPACE -> "next"
                KeyCode.ESCAPE -> "exit"
                else -> ""
            }
        }

        window.addKeyPressedHandler {
            actionManager.startUsing(inputTranslator.translate(it))
        }

        window.addKeyReleasedHandler {
            actionManager.stopUsing(inputTranslator.translate(it))
        }

        val simulator = StoryBoardSimulator(144.0, {
            engine.render(window.graphicsContext, 0.0, 0.0)
        }, { time, elapsedTime ->
            engine.process(actionManager)
            engine.update(time, elapsedTime)
        })

        simulator.simulate()
        stage.scene = window
        stage.show()
    }

}

