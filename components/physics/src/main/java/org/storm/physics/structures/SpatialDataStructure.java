package org.storm.physics.structures;

import org.storm.physics.entity.Entity;
import org.storm.physics.transforms.TransformableRender;

import java.util.Set;

/**
 * A SpatialDataStructure is a data structure which helps determine closeness of physics entities in 2D space. It is also
 * able to render itself for debug purposes.
 */
public interface SpatialDataStructure extends TransformableRender {

  /**
   * @param e Entity to insert
   * @return true if the insert was successful, false otherwise
   */
  boolean insert(Entity e);

  /**
   * @param e Entity to remove
   * @return true if the remove was successful, false otherwise
   */
  boolean remove(Entity e);

  /**
   * Clears the data structure of all entities
   */
  void clear();

  /**
   * @param e Entity check neighbours for
   * @return set of entities which are close spatially to e
   */
  Set<Entity> getCloseNeighbours(Entity e);
}
