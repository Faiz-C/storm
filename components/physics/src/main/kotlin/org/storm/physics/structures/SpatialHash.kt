package org.storm.physics.structures

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * A SpatialHash is a data structure which excels in handling worlds bounded by grids such as tile based games.
 */
class SpatialHash(
    private val cellSize: Double,
    private val width: Double,
    private val height: Double
): SpatialDataStructure {

    private val rows = (this.height + this.cellSize - 1) / cellSize
    private val cols = (this.width + this.cellSize - 1) / cellSize

    private val buckets = Array((rows * cols).toInt()) { mutableListOf<Collider>() }

    override fun insert(
        collider: Collider,
        boundary: CollidableShape
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(
        collider: Collider,
        boundary: CollidableShape
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getCloseNeighbours(
        collider: Collider,
        boundary: CollidableShape
    ): Map<CollidableShape, Collider> {
        TODO("Not yet implemented")
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        TODO("Not yet implemented")
    }
}