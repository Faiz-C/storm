package org.storm.impl.jfx.graphics

import javafx.application.Application
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents

class JfxWindowTest: Application() {

    override fun start(stage: Stage) {
        val window = JfxWindow()

        EventManager.registerJfxKeyEvents(window)

        runBlocking {
            EventManager.getJfxKeyEventStream().addConsumer {
                when (it.eventType) {
                    KeyEvent.KEY_PRESSED -> {
                        println("Key Pressed: ${it.text}")
                    }
                    KeyEvent.KEY_RELEASED -> {
                        println("Key Released: ${it.text}")
                    }
                }
            }
        }

        stage.scene = window
        stage.show()
    }

}