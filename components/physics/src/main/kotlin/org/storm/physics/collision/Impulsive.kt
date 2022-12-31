package org.storm.physics.collision

import org.storm.physics.entity.Entity

/**
 * An Impulsive Object is one which reacts when being collided with
 */
interface Impulsive {

  /**
   * Executes a reaction based on the Entity.
   *
   * @param entity the Entity which collided with this one
   */
  fun react(entity: Entity)

}
