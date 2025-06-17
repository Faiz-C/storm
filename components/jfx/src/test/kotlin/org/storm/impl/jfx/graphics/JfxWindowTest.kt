package org.storm.impl.jfx.graphics

import javafx.application.Application
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.graphics.Resolution
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.getJfxMouseEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents
import org.storm.impl.jfx.extensions.registerJfxMouseEvents

class JfxWindowTest: Application() {

    override fun start(stage: Stage) {
        val window = JfxWindow(Resolution.FHD)

        EventManager.registerJfxKeyEvents(window)
        EventManager.registerJfxMouseEvents(window)

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

            EventManager.getJfxMouseEventStream().addConsumer {
                when (it.eventType) {
                    MouseEvent.MOUSE_PRESSED -> {
                        println("Mouse Pressed: ${it.button}, (${it.x}, ${it.y})")
                    }
                    MouseEvent.MOUSE_RELEASED -> {
                        println("Mouse Released: ${it.button}, (${it.x}, ${it.y})")
                    }
                    MouseEvent.MOUSE_MOVED -> {
                        println("Mouse Moved: (${it.x}, ${it.y})")
                    }
                }
            }
        }

        stage.scene = window
        stage.show()
    }

}