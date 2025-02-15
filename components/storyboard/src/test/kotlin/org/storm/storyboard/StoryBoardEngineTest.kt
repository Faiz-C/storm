package org.storm.storyboard

import com.fasterxml.jackson.core.type.TypeReference
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.CoreContext
import org.storm.core.context.YAML_MAPPER
import org.storm.core.event.EventManager
import org.storm.core.input.InputBindings
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.core.ui.impl.JfxWindow
import org.storm.storyboard.helpers.StoryBoardSimulator

class StoryBoardEngineTest : Application() {

    override fun start(stage: Stage) {
        CoreContext.loadMappers()

        val engine = StoryBoardEngine()
        val batsScene = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/scene/bats.yml"), object: TypeReference<List<StoryBoardState>>() {})
        engine.loadScene(batsScene)
        engine.switchState("top-left")

        val window = JfxWindow()

        val actionManager = ActionManager()

        val inputActionInputBindings = InputBindings<KeyEvent> { t ->
            when (t.code) {
                KeyCode.ENTER -> "progress"
                KeyCode.ESCAPE -> "exit"
                KeyCode.UP -> "up"
                KeyCode.DOWN -> "down"
                else -> ""
            }
        }

        runBlocking {
            EventManager.getEventStream<KeyEvent>(JfxWindow.KEY_EVENT_STREAM).addConsumer {
                val action = inputActionInputBindings.getAction(it) ?: return@addConsumer
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> actionManager.submitActionEvent(ActionEvent(action, true))
                    KeyEvent.KEY_RELEASED -> actionManager.submitActionEvent(ActionEvent(action, false))
                }
            }
        }

        val simulator = StoryBoardSimulator(144.0, {
            window.canvas.clear()
            engine.render(window.canvas, 0.0, 0.0)
        }) { time, elapsedTime ->
            actionManager.updateActiveFrames()
            engine.process(actionManager.getStateSnapshot())
            engine.update(time, elapsedTime)
        }

        simulator.simulate()

        stage.scene = window
        stage.show()
    }

}
