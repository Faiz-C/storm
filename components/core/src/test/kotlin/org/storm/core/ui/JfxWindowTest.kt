package org.storm.core.ui

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.input.action.ActionEvent
import org.storm.core.input.action.ActionManager
import org.storm.core.input.InputBindings
import org.storm.core.ui.impl.JfxWindow

class JfxWindowTest: Application() {

    override fun start(stage: Stage) {
        val actionManager = ActionManager()
        val inputBindings: InputBindings<KeyEvent> = InputBindings<KeyEvent> {
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
            EventManager.getEventStream<KeyEvent>(JfxWindow.KEY_EVENT_STREAM).addConsumer {
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