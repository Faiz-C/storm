package org.storm.physics.structures

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.CollidableShape
import kotlin.math.floor

/**
 * A chunked spatial hash is a data structure which bridges the gap between a fixed sized spatial hash and an unbounded
 * spatial hash. By breaking down space in terms of chunks we are able to avoid issues with
 */
class ChunkedSpatialHash(
    private val cellSize: Double,
    private val chunkCells: Int
): SpatialDataStructure {

    val chunkSize = this.cellSize * this.chunkCells

    private val buckets = Array(rows * cols) { mutableListOf<Collider>() }

    override fun insert(
        collider: Collider,
        boundary: CollidableShape
    ): Boolean {
        val (minX, maxX, minY, maxY) = this.calculateCoordinateBounds(boundary.aabbBounds())

        for (cy in minX.toInt()..minY.toInt()) {
        }


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

    private fun calculateCoordinateBounds(aabb: AABB): List<Double> {
        val colsD = this.cols.toDouble()
        val minX = (aabb.x / this.cellSize).coerceIn(0.0, this.cols - 1)
        val minY = (aabb.y / this.cellSize).coerceIn(0.0, this.rows - 1)
        val maxX = ((aabb.x + aabb.width) / this.cellSize).coerceIn(0.0, this.cols - 1)
        val maxY = ((aabb.y + aabb.height) / this.cellSize).coerceIn(0.0, this.rows - 1)

        return listOf(minX.toInt(), maxX.toInt(), minY, maxY)
    }

    private fun doubleToCell(): Int {
        return floor(this / this@ChunkedSpatialHash.cellSize).toInt()
    }
}