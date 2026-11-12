package org.storm.impl.jfx.graphics

import javafx.scene.Scene
import javafx.scene.layout.Pane
import org.storm.core.context.Context
import org.storm.core.context.CoreContext
import org.storm.core.context.RESOLUTION
import org.storm.core.context.getContextEventStream
import org.storm.core.event.EventManager
import org.storm.core.graphics.Resolution
import org.storm.core.graphics.render.RenderContext
import org.storm.core.graphics.render.RenderingEngine
import javafx.scene.canvas.Canvas as SceneCanvas

/**
 * An implementation of a Window using Java FX. Key (KeyEvent) and Mouse (MouseEvent) inputs are available to be listened
 * to through the EventManager.
 */
class JfxWindow(resolution: Resolution = Context.RESOLUTION) : Scene(Pane()) {

    private val sceneCanvas: SceneCanvas = SceneCanvas()
    private val pane: Pane = (this.root as Pane).also {
        it.children.add(this.sceneCanvas)
    }

    var resolution: Resolution = resolution
        private set(value) {
            field = value
            pane.setMaxSize(value.width, value.height)
            pane.setMinSize(value.width, value.height)
            sceneCanvas.width = value.width
            sceneCanvas.height = value.height
        }

    var renderingEngine: RenderingEngine = JfxRenderingEngine(gc = sceneCanvas.graphicsContext2D)
        private set

    init {
        this.resolution = resolution // trigger the setter

        EventManager.getContextEventStream().subscribe {
            if (it.hasSettingChanged(CoreContext.RESOLUTION)) {
                this.resolution = Context.RESOLUTION
                this.renderingEngine = JfxRenderingEngine(gc = sceneCanvas.graphicsContext2D)
            }
        }
    }

}