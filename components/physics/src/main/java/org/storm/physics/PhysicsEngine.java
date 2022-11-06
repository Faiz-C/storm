package org.storm.physics;

import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import lombok.Setter;
import org.storm.core.render.Renderable;
import org.storm.core.update.Updatable;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.Vector;
import org.storm.physics.structures.SpatialDataStructure;
import org.storm.physics.transforms.UnitConvertor;

import java.util.ArrayList;
import java.util.List;


/**
 * A PhysicsEngine is an abstract representation of a game engine's physics core. A PhysicsEngine deals with applying
 * forces to Entities within the game and dealing with standard collisions. Physics using this engine are done using
 * arbitrary units in the form of doubles, they may not be 1:1 with pixels.
 *
 * By Default the conversion from units to pixels is 1:100 but a specific unit conversion can be supplied.
 */
public abstract class PhysicsEngine implements Updatable, Renderable {

  private static final double INFINITE_DURATION = Double.NEGATIVE_INFINITY;
  private static final double MINIMUM_REST_VELOCITY = 0.0001; // In pixels as the unit conversion is up to the User

  protected final Object entityLock;
  protected final SpatialDataStructure collisionStructure;
  protected final UnitConvertor unitConvertor;

  protected List<Entity> entities;

  @Getter
  @Setter
  protected boolean paused;

  protected PhysicsEngine(SpatialDataStructure collisionStructure, UnitConvertor unitConvertor) {
    this.entityLock = new Object();
    this.paused = false;
    this.collisionStructure = collisionStructure;
    this.unitConvertor = unitConvertor;
    this.entities = new ArrayList<>();
  }

  protected PhysicsEngine(SpatialDataStructure collisionStructure) {
    this(collisionStructure, new UnitConvertor() {});
  }

  /**
   * Adds the given Entities to the PhysicsEngine to have their physics handled.
   *
   * @param entities a list of Entities to have physics handled for
   */
  public void setEntities(List<Entity> entities) {
    synchronized (this.entityLock) {
      this.entities = entities;
      this.collisionStructure.clear();
      this.resetCollisionData();
    }
  }

  @Override
  public void update(double time, double elapsedTime) {
    if (this.isPaused()) return;

    synchronized (this.entityLock) {
      this.resetCollisionData();
      this.entities.forEach(entity -> this.processPhysics(entity, elapsedTime));
    }
  }

  @Override
  public void render(GraphicsContext gc, double x, double y) {
    this.collisionStructure.transform(this.unitConvertor).render(gc, 0, 0);
  }

  /**
   * Resets all collision data for all entities.
   */
  protected void resetCollisionData() {
    this.collisionStructure.clear();
    this.entities
      .parallelStream()
      .forEach(entity -> {
        entity.getCollisionState().clear();
        this.collisionStructure.insert(entity);
      });
  }

  /**
   * Clears all forces from all entities being tracked
   */
  public void clearAllForces() {
    synchronized (this.entityLock) {
      this.entities.forEach(entity -> entity.getActingForces().clear());
    }
  }

  /**
   * Handles collision checks for the given Entity and returns the MTV to allow for adjustments in case of collisions.
   * If no collisions occur, a zero vector should be returned.
   *
   * @param entity Entity to check collisions for
   */
  protected abstract void processCollisions(Entity entity);

  /**
   * Processes standard Physics for a given entity based on its physics data up to this point.
   *
   * @param entity Entity to process physics for
   * @param elapsedTime elapsed time (in seconds) since the physics of this entity was last processed
   */
  private void processPhysics(Entity entity, double elapsedTime) {
    // Apply all forces onto entity
    this.applyForces(entity, elapsedTime);

    // If the entity has zero or extremely little velocity then consider it at rest. This means we DON'T check collision
    double minimumRestVelocity = this.unitConvertor.toUnits(MINIMUM_REST_VELOCITY);
    if (entity.getVelocity().getSquaredMagnitude() < minimumRestVelocity * minimumRestVelocity) return;

    // Check and process any collisions
    this.processCollisions(entity);

    // Translate the entity by the forces applied to it
    entity.translateByVelocity();
  }

  /**
   * Applies all forces to the given Entity.
   *
   * @param entity Entity to apply forces for
   * @param elapsedTime elapsed time (in seconds) since forces were last applied to this Entity
   */
  private void applyForces(Entity entity, double elapsedTime) {
    entity.getActingForces().replaceAll((force, remainingDuration) -> {

      // Force = Mass * Acceleration. In 2D we have acceleration in both x and y directions.

      double aX = force.getX() / entity.getMass(); // x acceleration
      double aY = force.getY() / entity.getMass(); // y acceleration

      entity.setVelocity(entity.getVelocity().add(new Vector(aX * elapsedTime, aY * elapsedTime)));

      return (remainingDuration == INFINITE_DURATION) ? INFINITE_DURATION : remainingDuration - elapsedTime;
    });

    entity.getActingForces()
      .entrySet()
      .removeIf(entry -> entry.getValue() != INFINITE_DURATION && entry.getValue() <= 0);
  }

}
