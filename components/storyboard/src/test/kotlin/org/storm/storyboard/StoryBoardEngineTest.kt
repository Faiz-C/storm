package org.storm.storyboard

import com.fasterxml.jackson.core.type.TypeReference
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.YAML_MAPPER
import org.storm.core.context.loadMappers
import org.storm.core.event.EventManager
import org.storm.core.input.ActionState
import org.storm.core.input.InputEvent
import org.storm.core.input.InputManager
import org.storm.core.input.InputTranslator
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

        val inputManager = InputManager()
        val bindings = mapOf(
            KeyCode.ENTER.name to "progress",
            KeyCode.ESCAPE.name to "exit",
            KeyCode.UP.name to "up",
            KeyCode.DOWN.name to "down"
        )
        val inputTranslator = InputTranslator { state ->
            val activeActions = state.activeInputs
                .filterKeys {
                    bindings.contains(it)
                }.mapKeys {
                    bindings[it.key]!!
                }

            ActionState(activeActions)
        }

        runBlocking {
            EventManager.getJfxKeyEventStream().addConsumer {
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> inputManager.processInput(InputEvent(it.code.name, it, true))
                    KeyEvent.KEY_RELEASED -> inputManager.processInput(InputEvent(it.code.name, it, false))
                }
            }
        }

        val simulator = StoryBoardSimulator(144.0, {
            window.canvas.clear()
            engine.render(window.canvas, 0.0, 0.0)
        }) { time, elapsedTime ->
            inputManager.updateInputState(time * 1000)
            engine.process(inputTranslator.getActionState(inputManager.getCurrentInputState()))
            engine.update(time, elapsedTime)
        }

        simulator.simulate()

        stage.scene = window
        stage.show()
    }

}
