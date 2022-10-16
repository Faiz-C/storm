package org.storm.engine.example;

import javafx.scene.paint.Color;
import org.storm.core.render.Renderable;
import org.storm.physics.entity.ImmovableEntity;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.transforms.UnitConvertor;

public class ImmovableRectEntity extends ImmovableEntity {

  public ImmovableRectEntity(double x, double y, double width, double height) {
    super(new AxisAlignedRectangle(x, y, width, height));
  }

  @Override
  public Renderable transform(UnitConvertor unitConvertor) {
    return (gc, x, y) -> {
      gc.setFill(Color.RED);
      super.transform(unitConvertor).render(gc, x, y);
    };
  }

}
