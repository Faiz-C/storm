package org.storm.impl.jfx.extensions

import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.event.EventStream
import org.storm.impl.jfx.graphics.JfxWindow

private const val KEY_EVENT_STREAM = "key-events"
private const val MOUSE_EVENT_STREAM = "mouse-events"

internal fun EventManager.registerJfxInputEvents(window: JfxWindow) {
    createEventStream<KeyEvent>(KEY_EVENT_STREAM).also { stream ->
        window.addEventHandler(KeyEvent.ANY) {
            runBlocking { stream.produce(it) }
        }
    }

    createEventStream<MouseEvent>(MOUSE_EVENT_STREAM).also { stream ->
        window.addEventHandler(MouseEvent.ANY) {
            runBlocking { stream.produce(it) }
        }
    }
}

// easy access
fun EventManager.getJfxKeyEventStream(): EventStream<KeyEvent> {
    return getEventStream(KEY_EVENT_STREAM)
}

// easy access
fun EventManager.getJfxMouseEventStream(): EventStream<MouseEvent> {
    return getEventStream(MOUSE_EVENT_STREAM)
}
