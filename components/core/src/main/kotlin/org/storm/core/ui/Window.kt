package org.storm.core.ui

import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

/**
 * A Window represents a common window which has a settable resolution and can be rendered onto. It can also listen
 * for and act on events from a user (i.e. mouse events and keyboard events).
 */
class Window(
  resolution: Resolution
) : Scene(Pane()) {

  private val canvas: Canvas = Canvas()
  private val pane: Pane = (this.root as Pane).also {
    it.children.add(this.canvas)
  }

  private var resolution: Resolution = resolution
    set(value) {
      field = value
      pane.setMaxSize(value.width, value.height)
      pane.setMinSize(value.width, value.height)
      canvas.width = value.width
      canvas.height = value.height
    }


  init {
    this.resolution = resolution
  }

  /**
   * @return GraphicsContext of the inner canvas
   */
  val graphicsContext: GraphicsContext get() = canvas.graphicsContext2D

  /**
   * Clears the canvas of the Window
   */
  fun clear() {
    graphicsContext.clearRect(0.0, 0.0, resolution.width, resolution.height)
  }

  /**
   * Adds the given Consumer to handle ANY key event
   *
   * @param handler handler for key event
   */
  fun addKeyHandler(handler: (KeyEvent) -> Unit) {
    addEventHandler(KeyEvent.ANY) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a KEY_PRESSED event
   *
   * @param handler handler for key event
   */
  fun addKeyPressedHandler(handler: (KeyEvent) -> Unit) {
    addEventHandler(KeyEvent.KEY_PRESSED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a KEY_RELEASED event
   *
   * @param handler handler for key event
   */
  fun addKeyReleasedHandler(handler: (KeyEvent) -> Unit) {
    addEventHandler(KeyEvent.KEY_RELEASED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a KEY_TYPED event
   *
   * @param handler handler for key event
   */
  fun addKeyTypedHandler(handler: (KeyEvent) -> Unit) {
    addEventHandler(KeyEvent.KEY_TYPED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle ANY mouse event
   *
   * @param handler handler for mouse event
   */
  fun addMouseHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.ANY) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a DRAG_DETECTED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseDragDetectedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.DRAG_DETECTED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_CLICKED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseClickedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_CLICKED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_DRAGGED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseDraggedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_DRAGGED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_ENTERED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseEnteredHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_ENTERED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_EXITED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseExitedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_EXITED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_ENTERED_TARGET event
   *
   * @param handler handler for mouse event
   */
  fun addMouseEnteredTargetHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_EXITED_TARGET event
   *
   * @param handler handler for mouse event
   */
  fun addMouseExitedTargetHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_EXITED_TARGET) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_MOVED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseMovedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_MOVED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_PRESSED event
   *
   * @param handler handler for mouse event
   */
  fun addMousePressedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_PRESSED) { t -> handler(t) }
  }

  /**
   * Adds the given Consumer to handle a MOUSE_RELEASED event
   *
   * @param handler handler for mouse event
   */
  fun addMouseReleasedHandler(handler: (MouseEvent) -> Unit) {
    addEventHandler(MouseEvent.MOUSE_RELEASED) { t -> handler(t) }
  }
}
