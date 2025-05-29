package org.storm.impl.jfx.graphics

import javafx.scene.Scene
import javafx.scene.layout.Pane
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.graphics.Resolution
import org.storm.core.graphics.Window
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.utils.observation.Observable
import org.storm.core.utils.observation.Observer
import javafx.scene.canvas.Canvas as SceneCanvas

/**
 * An implementation of a Window using Java FX. Key (KeyEvent) and Mouse (MouseEvent) inputs are available to be listened
 * to through the EventManager.
 */
class JfxWindow : Scene(Pane()), Window, Observer {

    companion object {
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
    }

    override fun update(o: Observable) {
        if (o !is Context) return

        this.resolution = o.RESOLUTION
    }
}