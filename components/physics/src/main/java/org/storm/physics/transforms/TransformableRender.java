package org.storm.physics.transforms;

import org.storm.core.render.Renderable;

/**
 * A TransformableRender is an object which can be transformed from physics units to a Renderable version using pixels.
 */
public interface TransformableRender {

  /**
   * @param unitConvertor the UnitConvertor to use for conversion from units to pixels
   * @return a Renderable which represents the original Physics object using pixels
   */
  Renderable transform(UnitConvertor unitConvertor);

  /**
   * This uses a default unit conversion formulas of pixels = units * 100 and units = pixels / 100
   * @return a Renderable which represents the original Physics object using pixels
   */
  default Renderable transform() {
    return this.transform(new UnitConvertor() {}); // Default conversion is 1 unit = 100 pixel
  }

}
