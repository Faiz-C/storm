package org.storm.physics.math.geometry.shapes;

import org.storm.physics.math.Interval;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.Geometric;
import org.storm.physics.math.geometry.LineSegment;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.transforms.TransformableRender;

import java.util.List;

/**
 * A Shape is a geometric representation of a (common) shape in Euclidean (2D) space
 */
public interface Shape extends Geometric, TransformableRender {

  /**
   * @return the edges of the shape as LineSegments if applicable
   */
  List<LineSegment> getEdges();

  /**
   * Calculates and returns the projection of the shape onto the given axis (in vector form). This
   * is used for collision detection.
   *
   * @param axis axis to project onto
   * @return an Interval representing the projection
   */
  Interval getProjection(Vector axis);

  /**
   * @return the center of the Shape if applicable
   */
  Point getCenter();

  /**
   * @param p Point to check
   * @return true if the given Point is contained within the shape
   */
  boolean contains(Point p);

}
