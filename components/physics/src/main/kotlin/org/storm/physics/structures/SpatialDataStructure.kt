package org.storm.physics.structures

import org.storm.core.graphics.Renderable
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * A SpatialDataStructure is a data structure which helps determine closeness of physics colliders in 2D space. It is also
 * able to render itself for debug purposes.
 */
interface SpatialDataStructure: Renderable {

    /**
     * @param collider Collider for which the boundary belongs too
     * @param boundary boundary Shape to insert
     * @return true if the insert was successful, false otherwise
     */
    fun insert(collider: Collider, boundary: CollidableShape): Boolean

    /**
     * @param collider Collider for which the boundary belongs too
     * @param boundary boundary Shape to remove
     * @return true if the remove was successful, false otherwise
     */
    fun remove(collider: Collider, boundary: CollidableShape): Boolean

    /**
     * Clears the data structure of all entities
     */
    fun clear()

    /**
     * @param collider Collider for which the boundary belongs too
     * @param boundary boundary Shape to find neighbours for
     * @return map of (Boundary -> Collider it belongs too) which are close spatially to the boundary
     */
    fun getCloseNeighbours(collider: Collider, boundary: CollidableShape): Map<CollidableShape, Collider>

}
