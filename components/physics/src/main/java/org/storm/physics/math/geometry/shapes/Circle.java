package org.storm.physics.math.geometry.shapes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.storm.core.render.Renderable;
import org.storm.physics.math.Interval;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.LineSegment;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.transforms.UnitConvertor;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class Circle implements Shape {

  private final Point center;
  private final double radius;
  private final double diameter;

  public Circle(double x, double y, double radius) {
    this(new Point(x, y), radius, radius * 2);
  }

  @Override
  public boolean contains(Point p) {
    double distance = this.center.getSquaredDistance(p);
    return distance <= this.radius * this.radius;
  }

  @Override
  public Renderable transform(UnitConvertor unitConvertor) {
    return (gc, x, y) -> {
      gc.fillOval(
        x + unitConvertor.toPixels(this.center.getX() - this.radius),
        y + unitConvertor.toPixels(this.center.getY() - this.radius),
        unitConvertor.toPixels(this.diameter),
        unitConvertor.toPixels(this.diameter)
      );
    };
  }

  @Override
  public void translate(double dx, double dy) {
    this.center.translate(dx, dy);
  }

  @Override
  public List<LineSegment> getEdges() {
    return Collections.emptyList();
  }

  @Override
  public Interval getProjection(Vector axis) {
    Vector c = this.center.toVector();
    double d = c.dot(axis);
    return new Interval(d - this.radius, d + this.radius);
  }

  @Override
  public String toString() {
    return String.format("Center %s, Radius %f", this.center, this.radius);
  }

}
