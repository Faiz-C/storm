package org.storm.physics.collision;

import org.apache.commons.math3.util.FastMath;
import org.storm.physics.constants.Vectors;
import org.storm.physics.math.Interval;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.LineSegment;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.physics.math.geometry.shapes.ConvexPolygon;
import org.storm.physics.math.geometry.shapes.Shape;

import java.util.HashSet;
import java.util.Set;

class MtvCalculator {

  private MtvCalculator() {}

  /**
   * Checks if the given shapes have collided and returns a Force representing the minimum translation vector.
   *
   * Collision Detection is handled via the Separation Axis Theorem. This algorithm works to cover the cases of
   * ConvexPolygon + ConvexPolygon and Circle + ConvexPolygon but not Circle + Circle
   *
   * @param s1 shape to check
   * @param s2 shape to check
   * @return a Vector representing the MTV if the two polygons have collided, ZERO_VECTOR otherwise
   */
  static Vector calcMtv(Shape s1, Shape s2) {
    Vector dv = Vectors.ZERO_VECTOR;
    double mag = Double.POSITIVE_INFINITY;

    Set<Vector> axes = getAxes(s1, s2);

    for (Vector axis : axes) {
      Interval projection1 = s1.getProjection(axis);
      Interval projection2 = s2.getProjection(axis);

      double overlap = projection1.getOverlap(projection2);

      if (overlap <= 0) {
        return Vectors.ZERO_VECTOR;
      }
      else if (overlap < mag) {
        mag = overlap;
        dv = axis;
      }

    }

    // Need to orient to face towards Shape 1 always

    // 1) Get the direction vector between the center of the shapes (s2.center - s1.center)
    // 2) Calculate the dot product between the vector found in (1) and the mtv
    // 3) If the dot product is positive the two are facing in the same direction, so we have to flip the mtv
    //    in order for it to be facing p1
    //
    // *NOTE* to keep things simple we always want to point our mtv towards s1

    Vector s1ToS2 = new Vector(s1.getCenter(), s2.getCenter());
    return (dv.dot(s1ToS2) > 0) ? dv.flip().scaleToMagnitude(mag) : dv.scaleToMagnitude(mag);
  }

  /**
   * Checks if the given circles collide and returns the minimum translation vector to allow for separation.
   *
   * @param c1 circle to check
   * @param c2 circle to check
   * @return a Vector representing the MTV if the two circles have collided, ZERO_VECTOR otherwise
   */
  static Vector calcCircleMtv(Circle c1, Circle c2) {
    double distanceBetweenCenters = c2.getCenter().getDistance(c1.getCenter());
    double radiusSum = c1.getRadius() + c2.getRadius();

    if (distanceBetweenCenters >= radiusSum) return Vectors.ZERO_VECTOR;

    double mtvAngle = FastMath.atan2(c2.getCenter().getY() - c1.getCenter().getY(),
      c2.getCenter().getX() - c1.getCenter().getX());
    double overlap = radiusSum - distanceBetweenCenters;

    return new Vector(-FastMath.cos(mtvAngle) * overlap, -FastMath.sin(mtvAngle) * overlap);
  }

  /**
   * @param s1 shape to get axes for
   * @param s2 shape to get axes for
   * @return a set of Vectors representing the separating axes to check
   */
  private static Set<Vector> getAxes(Shape s1, Shape s2) {

    Set<Vector> axes = new HashSet<>();

    for (LineSegment edge : s1.getEdges()) {
      axes.add(edge.getVectorFromStart().getClockwiseNormal().getNormalizedForm());
    }

    for (LineSegment edge : s2.getEdges()) {
      axes.add(edge.getVectorFromStart().getClockwiseNormal().getNormalizedForm());
    }

    // Special Case for Circles, need to include a special axis
    if (s1.getEdges().isEmpty()) {
      axes.add(new Vector(s1.getCenter(), ((ConvexPolygon) s2).getClosestVertex(s1.getCenter()))
        .getClockwiseNormal().getNormalizedForm());
    }
    else if (s2.getEdges().isEmpty()) {
      axes.add(new Vector(s2.getCenter(), ((ConvexPolygon) s1).getClosestVertex(s2.getCenter()))
        .getClockwiseNormal().getNormalizedForm());
    }

    return axes;
  }

}
