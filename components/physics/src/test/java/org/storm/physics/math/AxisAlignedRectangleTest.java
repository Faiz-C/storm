package org.storm.physics.math;

import org.junit.jupiter.api.Test;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AxisAlignedRectangleTest {

  @Test
  public void testIntersects() {
    AxisAlignedRectangle axisAlignedRectangle = new AxisAlignedRectangle(0, 0, 10, 10);
    AxisAlignedRectangle axisAlignedRectangle2 = new AxisAlignedRectangle(5, 5, 10, 10);

    assertTrue(axisAlignedRectangle.intersects(axisAlignedRectangle2));
    assertTrue(axisAlignedRectangle2.intersects(axisAlignedRectangle));

    AxisAlignedRectangle axisAlignedRectangle3 = new AxisAlignedRectangle(12, 12, 2, 2);

    assertFalse(axisAlignedRectangle.intersects(axisAlignedRectangle3));
    assertFalse(axisAlignedRectangle3.intersects(axisAlignedRectangle));
  }

}
