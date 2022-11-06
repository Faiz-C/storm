package org.storm.physics.transforms;

public interface UnitConvertor {

  default double toPixels(double units) {
    return units * 10;
  }

  default double toUnits(double pixels) {
    return pixels / 10;
  }

}
