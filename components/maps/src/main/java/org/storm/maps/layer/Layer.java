package org.storm.maps.layer;

import lombok.Getter;
import lombok.Setter;
import org.storm.core.render.Renderable;
import org.storm.core.ui.Resolution;
import org.storm.core.update.Updatable;
import org.storm.physics.entity.Entity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public abstract class Layer implements Renderable, Updatable {

  protected final Set<Entity> entities;

  protected boolean active;

  protected Resolution resolution;

  public Layer(Set<Entity> entities, boolean active, Resolution resolution) {
    this.entities = entities;
    this.active = active;
    this.resolution = resolution;
  }

  public Layer(boolean active, Resolution resolution) {
    this(new HashSet<>(), active, resolution);
  }

  public void addEntity(Entity entity) {
    this.entities.add(entity);
  }

  public void removeEntity(Entity entity) {
    this.entities.remove(entity);
  }
}
