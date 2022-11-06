package org.storm.maps.tile;

import org.storm.physics.entity.ImmovableEntity;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;

public class Tile extends ImmovableEntity {

  public Tile(double x, double y, double width, double height) {
    super(new AxisAlignedRectangle(x, y, width, height));
  }

}
