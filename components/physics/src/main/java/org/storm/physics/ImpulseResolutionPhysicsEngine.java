package org.storm.physics;

import lombok.Getter;
import org.apache.commons.math3.util.FastMath;
import org.storm.core.ui.Resolution;
import org.storm.physics.collision.CollisionDetector;
import org.storm.physics.constants.Vectors;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.Vector;
import org.storm.physics.structures.QuadrantTree;
import org.storm.physics.transforms.UnitConvertor;


/**
 * A ImpulseResolutionPhysicsEngine is straight forward and basic implementation of a PhysicsEngines. It uses a Quad Tree (QuadrantTree)
 * to check likely collisions and then uses impulse resolution to deal with collisions.
 */
@Getter
public class ImpulseResolutionPhysicsEngine extends PhysicsEngine {

  private static final double POSITIONAL_CORRECTION_ADJUSTMENT = 0.2; // This is in pixels as the unit conversion is up to the user
  private static final double POSITIONAL_CORRECTION_THRESHOLD = 0.01; // This is in pixels as the unit conversion is up to the user

  public ImpulseResolutionPhysicsEngine(Resolution resolution, UnitConvertor unitConvertor) {
    super(
      new QuadrantTree(
        0,
        unitConvertor.toUnits(resolution.getWidth()),
        unitConvertor.toUnits(resolution.getHeight())
      ),
      unitConvertor
    );
  }

  public ImpulseResolutionPhysicsEngine(Resolution resolution) {
    this(resolution, new UnitConvertor() {});
  }

  @Override
  protected void processCollisions(Entity entity) {
    this.collisionStructure.getCloseNeighbours(entity)
      .forEach(other -> {

        // Ignore if already interacted with
        if (other.getCollisionState().containsKey(entity)) return;

        // Initially we can say that these two did not collide, but still track that we checked the two
        entity.getCollisionState().put(other, false);

        // Check collision and get back the minimum translation vector
        Vector mtv = CollisionDetector.checkMtv(entity.getHurtBox(), other.getHurtBox());

        // no collision happened
        if (mtv == Vectors.ZERO_VECTOR) return;

        // resolve collision via impulse resolution
        impulsiveResolution(entity, other, mtv);

        // Allow the entities to react to colliding with each other
        entity.react(other);
        other.react(entity);

        // Update the collision state to say that we collided with this entity
        entity.getCollisionState().put(other, true);
      });
  }

  /**
   * Resolves the collision between the two entities using impulse resolution.
   *
   * @param e1 Entity involved in the collision
   * @param e2 Entity involved in the collision
   * @param mtv minimum translation vector calculated from the collision
   */
  private void impulsiveResolution(Entity e1, Entity e2, Vector mtv) {
    Vector collisionNormal = mtv.getNormalizedForm();
    Vector relativeVelocity = e1.getVelocity().subtract(e2.getVelocity());
    double contactVelocity = relativeVelocity.dot(collisionNormal);

    // Ignore if the velocities are moving away
    if (contactVelocity > 0) return;

    // Calculate Impulse vector
    double minRestitution = FastMath.min(e1.getRestitution(), e2.getRestitution()); // minimum restitution constant is used
    double impulse = (-(1.0 + minRestitution) * contactVelocity) / (e1.getInverseMass() + e2.getInverseMass());
    Vector impulseVector = collisionNormal.scale(impulse);

    // Apply impulse vectors respectively
    e1.setVelocity(e1.getVelocity().add(impulseVector.scale(e1.getInverseMass())));
    e2.setVelocity(e2.getVelocity().subtract(impulseVector.scale(e2.getInverseMass())));

    // Positional correction do to eventual floating point arithmetic error
    double mag = FastMath.max(mtv.getMagnitude() - this.unitConvertor.toUnits(POSITIONAL_CORRECTION_THRESHOLD), 0.0);
    double correction = (mag / (e1.getInverseMass() + e2.getInverseMass())) * this.unitConvertor.toUnits(POSITIONAL_CORRECTION_ADJUSTMENT);

    e1.translate(collisionNormal.scale(correction * e1.getInverseMass()));
    e2.translate(collisionNormal.scale(-correction * e2.getInverseMass()));
  }

}
