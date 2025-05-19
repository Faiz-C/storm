package org.storm.impl.jfx.graphics

import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.event.EventManager
import org.storm.core.event.EventStream
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.Resolution
import org.storm.core.graphics.Window
import org.storm.core.utils.observation.Observable
import org.storm.core.utils.observation.Observer
import javafx.scene.canvas.Canvas as SceneCanvas

/**
 * An implementation of a Window using Java FX. Key (KeyEvent) and Mouse (MouseEvent) inputs are available to be listened
 * to through the EventManager.
 */
class JfxWindow : Scene(Pane()), Window, Observer {

    companion object {
        private const val KEY_EVENT_STREAM = "key-events"
        private const val MOUSE_EVENT_STREAM = "mouse-events"
    }

    private val sceneCanvas: SceneCanvas = SceneCanvas()
    private val pane: Pane = (this.root as Pane).also {
        it.children.add(this.sceneCanvas)
    }

    override var resolution: Resolution = Context.RESOLUTION
        set(value) {
            field = value
            pane.setMaxSize(value.width, value.height)
            pane.setMinSize(value.width, value.height)
            sceneCanvas.width = value.width
            sceneCanvas.height = value.height
        }

    override val canvas: Canvas = JfxCanvas(sceneCanvas.graphicsContext2D)

    init {
        this.resolution = resolution
        Context.addObserver(this)

        EventManager.createEventStream<KeyEvent>(KEY_EVENT_STREAM).also { stream ->
            addEventHandler(KeyEvent.ANY) {
                runBlocking { stream.produce(it) }
            }
        }

        EventManager.createEventStream<MouseEvent>(MOUSE_EVENT_STREAM).also { stream ->
            addEventHandler(MouseEvent.ANY) {
                runBlocking { stream.produce(it) }
            }
        }
    }

    override fun update(o: Observable) {
        if (o !is Context) return

        this.resolution = o.RESOLUTION
    }

}

// easy access
fun EventManager.getJfxKeyEventStream(): EventStream<KeyEvent> {
    return getEventStream("key-events")
}

// easy access
fun EventManager.getJfxMouseEventStream(): EventStream<MouseEvent> {
    return getEventStream("mouse-events")
}