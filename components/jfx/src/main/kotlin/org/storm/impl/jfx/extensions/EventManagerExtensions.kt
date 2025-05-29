package org.storm.impl.jfx.extensions

import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.runBlocking
import org.storm.core.event.EventManager
import org.storm.core.event.EventStream
import org.storm.impl.jfx.graphics.JfxWindow

private const val KEY_EVENT_STREAM = "key-events"
private const val MOUSE_EVENT_STREAM = "mouse-events"

/**
 * Registers the key events emitted by the given window as an event stream within the engine
 *
 * @param window JfxWindow to listen for key events from
 */
fun EventManager.registerJfxKeyEvents(window: JfxWindow) {
    createEventStream<KeyEvent>(KEY_EVENT_STREAM).also { stream ->
        window.addEventHandler(KeyEvent.ANY) {
            runBlocking { stream.produce(it) }
        }
    }
}

/**
 * Registers the mouse events emitted by the given window as an event stream within the engine
 *
 * @param window JfxWindow to listen for mouse events from
 */
fun EventManager.registerJfxMouseEvents(window: JfxWindow) {
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
