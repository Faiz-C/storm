package org.storm.physics.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.storm.core.render.Renderable;
import org.storm.physics.collision.Impulsive;
import org.storm.physics.constants.Vectors;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.Geometric;
import org.storm.physics.math.geometry.shapes.Shape;
import org.storm.physics.transforms.TransformableRender;
import org.storm.physics.transforms.UnitConvertor;

import java.util.HashMap;
import java.util.Map;

/**
 * An Entity represents an abstract object which exists in 2D space. It can have forces applied to it, collide with
 * other Entities and react when collided with. An entity experiences standard physics as well.
 */
@Getter
@Setter
public abstract class Entity implements Impulsive, Geometric, TransformableRender {

  private static final double INFINITE_DURATION = Double.NEGATIVE_INFINITY;

  protected final Map<Vector, Double> actingForces;
  protected final Map<Entity, Boolean> collisionState;
  protected Shape hurtBox;
  protected double speed;
  protected Vector velocity;
  protected double mass;
  protected double restitution;

  @Setter(AccessLevel.NONE)
  protected double inverseMass;

  protected Entity(@NonNull Shape hurtBox, double speed, double mass, double restitution) {
    if (restitution > 1 || restitution < 0) {
      throw new IllegalArgumentException("restitution must be in the range [0, 1]");
    }

    if (speed < 0) {
      throw new IllegalArgumentException("speed cannot be lower than 0");
    }

    this.hurtBox = hurtBox;
    this.speed = speed;
    this.restitution = restitution;
    this.velocity = Vectors.ZERO_VECTOR;
    this.actingForces = new HashMap<>();
    this.collisionState = new HashMap<>();
    this.setMass(mass);
  }

  public void setMass(double mass) {
    if (mass <= 0) {
      throw new IllegalArgumentException("mass must be > 0");
    }
    this.mass = mass;
    this.inverseMass = 1.0 / mass;
  }

  /**
   * Translates the Entity's position by the given velocity Vector
   *
   * @param vector velocity Vector to translate by
   */
  public void translate(Vector vector) {
    this.translate(vector.getX(), vector.getY());
  }

  /**
   * Translates the Entity's position by its current velocity
   */
  public void translateByVelocity() {
    this.translate(this.velocity);
  }

  /**
   * Adds the given force (in Vector form) to be applied for an infinite duration.
   *
   * @param force force to apply (as a Vector)
   */
  public void addForce(Vector force) {
    this.addForce(force, INFINITE_DURATION);
  }

  /**
   * Adds the given force (in Vector form) to be applied for given duration. Applying the same force
   * more than once will result in increasing the duration in which the force will be applied for.
   *
   * @param force force to apply (as a Vector)
   * @param duration how long (in seconds) to apply the force for
   */
  public void addForce(Vector force, double duration) {
    this.actingForces.computeIfPresent(force, (f, remainingDuration) -> remainingDuration + duration);
    this.actingForces.putIfAbsent(force, duration);
  }

  /**
   * Removes the force (in Vector form) from the given Entity. i.e. stops applying the force permanently.
   *
   * @param force force to remove/stop applying (as a Vector)
   */
  public void removeForce(Vector force) {
    this.actingForces.remove(force);
  }

  /**
   * Clears all forces being applied to the given Entity. i.e. stops applying any forces to the Entity
   *
   */
  public void clearForces() {
    this.actingForces.clear();
  }

  @Override
  public Renderable transform(UnitConvertor unitConvertor) {
    return (gc, x, y) -> {
      this.getHurtBox().transform(unitConvertor).render(gc, x, y);
    };
  }

  @Override
  public void translate(double dx, double dy) {
    this.hurtBox.translate(dx, dy);
  }

  @Override
  public void react(Entity entity) {
    // Abstract Entity by default doesn't react
  }

  @Override
  public String toString() {
    return String.format("{Hurtbox: %s, Velocity: %s, Mass: %f, Restitution: %f}",
      this.hurtBox, this.velocity, this.mass, this.restitution);
  }
}
