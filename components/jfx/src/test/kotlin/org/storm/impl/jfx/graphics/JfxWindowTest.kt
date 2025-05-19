package org.storm.impl.jfx.graphics

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.input.InputBindings
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.impl.jfx.graphics.getJfxKeyEventStream

class JfxWindowTest: Application() {

    override fun start(stage: Stage) {
        val actionManager = ActionManager()
        val inputBindings: InputBindings<KeyEvent> = InputBindings {
            when (it.code) {
                KeyCode.UP -> "up"
                KeyCode.DOWN -> "down"
                KeyCode.LEFT -> "left"
                KeyCode.RIGHT -> "right"
                else -> null
            }
        }

        val window = JfxWindow()

        runBlocking {
            EventManager.getJfxKeyEventStream().addConsumer {
                val action = inputBindings.getAction(it) ?: return@addConsumer
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> actionManager.submitActionEvent(ActionEvent(action, true))
                    KeyEvent.KEY_RELEASED -> actionManager.submitActionEvent(ActionEvent(action, false))
                }
            }
        }

        stage.scene = window
        stage.show()
    }

}