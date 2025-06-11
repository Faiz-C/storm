package org.storm.storyboard

import com.fasterxml.jackson.core.type.TypeReference
import javafx.application.Application
import javafx.event.Event
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.YAML_MAPPER
import org.storm.core.context.loadMappers
import org.storm.core.event.EventManager
import org.storm.core.input.InputBindings
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.storyboard.helpers.StoryBoardSimulator

class StoryBoardEngineTest : Application() {

    override fun start(stage: Stage) {
        Context.loadMappers()

        val engine = StoryBoardEngine()
        val batsScene = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/scene/bats.yml"), object: TypeReference<List<StoryBoardState>>() {})
        engine.loadScene(batsScene)
        engine.switchState("top-left")

        val window = JfxWindow()

        EventManager.registerJfxKeyEvents(window)

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
            EventManager.getJfxKeyEventStream().addConsumer {
                val action = inputActionInputBindings.getAction(it) ?: return@addConsumer
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> actionManager.submitActionEvent(ActionEvent(action, true, it))
                    KeyEvent.KEY_RELEASED -> actionManager.submitActionEvent(ActionEvent(action, false, it))
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
