package org.storm.storyboard

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import org.storm.core.asset.AssetManager
import org.storm.core.asset.source.AssetSource
import org.storm.core.asset.source.context.LocalStorageAssetContextBuilder
import org.storm.core.asset.source.loaders.localstorage.YamlLocalStorageAssetLoader
import org.storm.core.input.Translator
import org.storm.core.input.action.SimpleActionManager
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window
import org.storm.storyboard.helpers.StoryBoardSimulator
import java.nio.file.Paths

class StoryBoardEngineTest: Application() {

    override fun start(stage: Stage) {
        val resourceDir = Paths.get("components", "storyboard", "src", "test", "resources", "scenes")

        val assetManager = AssetManager()
        val assetContextBuilder = LocalStorageAssetContextBuilder(resourceDir.toString(), "yml")

        assetManager.registerSource(AssetSource(
            "local-storage",
            assetContextBuilder,
            listOf(
                YamlLocalStorageAssetLoader()
            )
        ))

        val story: StoryBoardState = assetManager.getAsset("bat-singular", "local-storage")
        println(story)

        val engine = StoryBoardEngine(assetManager = assetManager, assetSourceId = "local-storage")
        engine.loadScene("bats")

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


fun main(args: Array<String>) {
    Application.launch(StoryBoardEngineTest::class.java, *args)
}

