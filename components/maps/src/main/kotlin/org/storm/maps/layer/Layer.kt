package org.storm.maps.layer

import org.storm.core.render.Renderable
import org.storm.core.ui.Resolution
import org.storm.core.update.Updatable
import org.storm.physics.entity.Entity

abstract class Layer(
  var active: Boolean,
  var resolution: Resolution,
  val entities: MutableSet<Entity> = mutableSetOf()
) : Renderable, Updatable {

  open fun addEntity(entity: Entity) {
    entities.add(entity)
  }

  open fun removeEntity(entity: Entity) {
    entities.remove(entity)
  }
}
