package org.storm.maps;

import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import org.storm.core.render.Renderable;
import org.storm.core.ui.Resolution;
import org.storm.core.update.Updatable;
import org.storm.maps.layer.Layer;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.geometry.Point;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Map implements Renderable, Updatable {

  private final Set<Layer> layers;

  @Getter
  private final Set<Entity> entities;

  private final Point renderPoint;

  private final Resolution resolution;

  public Map(Resolution resolution) {
    this.renderPoint = new Point(0, 0); // Top left corner of the screen
    this.layers = new LinkedHashSet<>();
    this.entities = new HashSet<>();
    this.resolution = resolution;
  }

  public void addLayer(Layer layer) {
    layer.setResolution(this.resolution);
    this.layers.add(layer);

    if (layer.isActive()) {
      this.entities.addAll(layer.getEntities());
    }
  }

  public void removeLayer(Layer layer) {
    this.layers.remove(layer);

    if (layer.isActive()) {
      this.entities.removeAll(layer.getEntities());
    }
  }

  public void setResolution(Resolution resolution) {
    this.layers
      .parallelStream()
      .forEach(layer -> layer.setResolution(resolution));
  }

  public void shiftRenderPoint(double dx, double dy) {
    this.renderPoint.translate(dx, dy);
  }

  @Override
  public void render(GraphicsContext graphicsContext, double x, double y) {
    this.layers
      .forEach(layer -> layer.render(graphicsContext, renderPoint.getX(), renderPoint.getY()));
  }

  @Override
  public void update(double time, double frameTime) {
    for (Layer layer : this.layers) {
      if (layer.isActive()) {
        layer.update(time, frameTime);
      }
    }
  }

}
