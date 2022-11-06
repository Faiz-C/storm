package org.storm.physics.math.geometry.shapes;

import org.storm.physics.math.geometry.Point;

/**
 * A Triangle is a ConvexPolygon with three sides and is the smallest possible ConvexPolygon.
 */
public class Triangle extends ConvexPolygon {

  public Triangle(Point a, Point b, Point c) {
    super(a, b, c);
  }

}
