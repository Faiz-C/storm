package org.storm.physics.structures

import org.storm.physics.entity.Entity
import org.storm.physics.transforms.TransformableRender

/**
 * A SpatialDataStructure is a data structure which helps determine closeness of physics entities in 2D space. It is also
 * able to render itself for debug purposes.
 */
interface SpatialDataStructure : TransformableRender {

  /**
   * @param e Entity to insert
   * @return true if the insert was successful, false otherwise
   */
  fun insert(e: Entity): Boolean

  /**
   * @param e Entity to remove
   * @return true if the remove was successful, false otherwise
   */
  fun remove(e: Entity): Boolean

  /**
   * Clears the data structure of all entities
   */
  fun clear()

  /**
   * @param e Entity check neighbours for
   * @return set of entities which are close spatially to e
   */
  fun getCloseNeighbours(e: Entity): Set<Entity>

}
