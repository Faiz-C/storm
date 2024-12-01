package org.storm.maps

import org.storm.core.render.canvas.Canvas
import org.storm.core.render.Renderable
import org.storm.core.render.geometry.Point
import org.storm.core.update.Updatable
import org.storm.maps.layer.Layer
import org.storm.physics.entity.Entity

class Map : Renderable, Updatable {

    // Top left corner of the screen
    private val renderPoint: Point = Point(0.0, 0.0)

    private val layers: MutableSet<Layer> = LinkedHashSet()

    val entities: MutableSet<Entity> = mutableSetOf()

    fun addLayer(layer: Layer) {
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

    fun shiftRenderPoint(dx: Double, dy: Double) {
        renderPoint.translate(dx, dy)
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        layers.forEach { layer -> layer.render(canvas, renderPoint.x, renderPoint.y) }
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        layers.filter {
            it.active
        }.forEach {
            it.update(time, elapsedTime)
        }
    }
}
