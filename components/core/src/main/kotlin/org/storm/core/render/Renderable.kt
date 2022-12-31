package org.storm.core.render

import javafx.scene.canvas.GraphicsContext

/**
 * A Renderable is an object which understands how to represent itself on a window or canvas. In other words,
 * it knows how to render for a user to see it.
 */
fun interface Renderable {

  /**
   * Renders the object onto the given GraphicsContext.
   *
   * @param gc GraphicsContext to render onto
   * @param x base x position to render from
   * @param y base y position to render from
   */
  fun render(gc: GraphicsContext, x: Double, y: Double)

}
