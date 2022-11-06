package org.storm.engine.example;

import javafx.scene.paint.Color;
import org.storm.core.render.Renderable;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.geometry.shapes.Shape;
import org.storm.physics.transforms.UnitConvertor;

public class EntityImpl extends Entity {

  public EntityImpl(Shape hurtBox, double speed, double mass, double restitution) {
    super(hurtBox, speed, mass, restitution);
  }

  @Override
  public Renderable transform(UnitConvertor unitConvertor) {
    return (gc, x, y) -> {
      gc.setFill(Color.BLACK);
      super.transform(unitConvertor).render(gc, x, y);
    };
  }

}
