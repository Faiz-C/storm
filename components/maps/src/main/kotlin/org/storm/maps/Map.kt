package org.storm.maps

import javafx.scene.canvas.GraphicsContext
import org.storm.core.render.Renderable
import org.storm.core.ui.Resolution
import org.storm.core.update.Updatable
import org.storm.maps.layer.Layer
import org.storm.physics.entity.Entity
import org.storm.physics.math.geometry.Point
import java.util.function.Consumer

class Map(
  private val resolution: Resolution
) : Renderable, Updatable {

  // Top left corner of the screen
  private val renderPoint: Point = Point(0.0, 0.0)

  private val layers: MutableSet<Layer> = LinkedHashSet()

  val entities: MutableSet<Entity> = mutableSetOf()

  fun addLayer(layer: Layer) {
    layer.resolution = resolution
    layers.add(layer)

    if (layer.active) {
      entities.addAll(layer.entities)
    }
  }

  fun removeLayer(layer: Layer) {
    layers.remove(layer)

    if (layer.active) {
      entities.removeAll(layer.entities)
    }
  }

  fun setResolution(resolution: Resolution) {
    layers.forEach { layer -> layer.resolution = resolution }
  }

  fun shiftRenderPoint(dx: Double, dy: Double) {
    renderPoint.translate(dx, dy)
  }

  override fun render(gc: GraphicsContext, x: Double, y: Double) {
    layers
      .forEach(Consumer { layer -> layer.render(gc, renderPoint.x, renderPoint.y) })
  }

  override fun update(time: Double, elapsedTime: Double) {
    layers.filter {
      it.active
    }.forEach {
      it.update(time, elapsedTime)
    }
  }
}
