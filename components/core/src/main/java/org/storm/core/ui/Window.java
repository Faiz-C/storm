package org.storm.core.ui;


import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * A Window represents a common window which has a settable resolution and can be rendered onto. It can also listen
 * for and act on events from a user (i.e. mouse events and keyboard events).
 */
public class Window extends Scene {

  @Getter
  private Resolution resolution;

  private final Pane pane;

  private final Canvas canvas;

  public Window(Resolution resolution) {
    super(new Pane());
    this.pane = (Pane) this.getRoot();
    this.canvas = new Canvas();
    this.pane.getChildren().add(this.canvas);
    this.setResolution(resolution);
  }

  /**
   * Sets the resolution of the window
   *
   * @param resolution wanted resolution
   */
  public void setResolution(Resolution resolution) {
    this.resolution = resolution;

    this.pane.setMaxSize(resolution.getWidth(), resolution.getHeight());
    this.pane.setMinSize(resolution.getWidth(), resolution.getHeight());

    this.canvas.setWidth(resolution.getWidth());
    this.canvas.setHeight(resolution.getHeight());
  }

  /**
   * @return GraphicsContext of the inner canvas
   */
  public GraphicsContext getGraphicsContext() {
    return this.canvas.getGraphicsContext2D();
  }

  /**
   * Clears the canvas of the Window
   */
  public void clear() {
    this.getGraphicsContext().clearRect(0, 0, this.resolution.getWidth(), this.resolution.getHeight());
  }

  /**
   * Adds the given Consumer to handle ANY key event
   *
   * @param handler handler for key event
   */
  public void addKeyHandler(Consumer<KeyEvent> handler) {
    this.addEventHandler(KeyEvent.ANY, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a KEY_PRESSED event
   *
   * @param handler handler for key event
   */
  public void addKeyPressedHandler(Consumer<KeyEvent> handler) {
    this.addEventHandler(KeyEvent.KEY_PRESSED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a KEY_RELEASED event
   *
   * @param handler handler for key event
   */
  public void addKeyReleasedHandler(Consumer<KeyEvent> handler) {
    this.addEventHandler(KeyEvent.KEY_RELEASED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a KEY_TYPED event
   *
   * @param handler handler for key event
   */
  public void addKeyTypedHandler(Consumer<KeyEvent> handler) {
    this.addEventHandler(KeyEvent.KEY_TYPED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle ANY mouse event
   *
   * @param handler handler for mouse event
   */
  public void addMouseHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.ANY, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a DRAG_DETECTED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseDragDetectedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.DRAG_DETECTED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_CLICKED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseClickedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_CLICKED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_DRAGGED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseDraggedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_DRAGGED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_ENTERED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseEnteredHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_ENTERED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_EXITED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseExitedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_EXITED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_ENTERED_TARGET event
   *
   * @param handler handler for mouse event
   */
  public void addMouseEnteredTargetHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_EXITED_TARGET event
   *
   * @param handler handler for mouse event
   */
  public void addMouseExitedTargetHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_MOVED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseMovedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_MOVED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_PRESSED event
   *
   * @param handler handler for mouse event
   */
  public void addMousePressedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_PRESSED, handler::accept);
  }

  /**
   * Adds the given Consumer to handle a MOUSE_RELEASED event
   *
   * @param handler handler for mouse event
   */
  public void addMouseReleasedHandler(Consumer<MouseEvent> handler) {
    this.addEventHandler(MouseEvent.MOUSE_RELEASED, handler::accept);
  }

}
