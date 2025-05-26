package org.storm.physics.structures

import org.storm.core.graphics.Renderable
import org.storm.physics.entity.PhysicsObject
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * A SpatialDataStructure is a data structure which helps determine closeness of physics entities in 2D space. It is also
 * able to render itself for debug purposes.
 */
interface SpatialDataStructure: Renderable {

    /**
     * @param e Entity for which the boundary belongs too
     * @param boundary boundary Shape to insert
     * @return true if the insert was successful, false otherwise
     */
    fun insert(e: PhysicsObject, boundary: CollidableShape): Boolean

    /**
     * @param e Entity for which the boundary belongs too
     * @param boundary boundary Shape to remove
     * @return true if the remove was successful, false otherwise
     */
    fun remove(e: PhysicsObject, boundary: CollidableShape): Boolean

    /**
     * Clears the data structure of all entities
     */
    fun clear()

    /**
     * @param e Entity for which the boundary belongs too
     * @param boundary boundary Shape to find neighbours for
     * @return map of (Shape -> Entity it belongs too) which are close spatially to the boundary
     */
    fun getCloseNeighbours(e: PhysicsObject, boundary: CollidableShape): Map<CollidableShape, PhysicsObject>

}
