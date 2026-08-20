package org.storm.physics.structures

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.CollidableShape
import kotlin.math.floor

/**
 * A chunked spatial hash is a data structure which bridges the gap between a fixed sized spatial hash and an unbounded
 * spatial hash. Each chunk is a [chunkCells] x [chunkCells] square. All values here are in *units*.
 */
class ChunkedSpatialHash<T>(
    private val cellSize: Double,
    private val chunkCells: Int
): SpatialDataStructure<T> {

    interface ReadOnlyChunk<T> {
        val boundary: AABB
        val items: List<T>
    }

    internal class Chunk<T>(val chunkCells: Int): ReadOnlyChunk<T> {
        override val boundary: AABB =

    }

    val chunkSize = this.cellSize * this.chunkCells

    private inner class Chunk {
        val buckets = Array(this@ChunkedSpatialHash.chunkCells * ) { mutableListOf<Collider>() }
    }


    override fun insert(
        item: T,
        boundary: CollidableShape
    ): Boolean {
        val (minX, maxX, minY, maxY) = this.calculateCoordinateBounds(boundary.aabbBounds())

        for (cy in minX.toInt()..minY.toInt()) {
        }


    }

    override fun remove(
        item: T,
        boundary: CollidableShape
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getCloseNeighbours(
        item: T,
        boundary: CollidableShape
    ): Map<CollidableShape, T> {
        TODO("Not yet implemented")
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    private fun calculateCoordinateBounds(aabb: AABB): List<Double> {
        val minX = (aabb.x / this.cellSize).coerceIn(0.0, this.cols - 1)
        val minY = (aabb.y / this.cellSize).coerceIn(0.0, this.rows - 1)
        val maxX = ((aabb.x + aabb.width) / this.cellSize).coerceIn(0.0, this.cols - 1)
        val maxY = ((aabb.y + aabb.height) / this.cellSize).coerceIn(0.0, this.rows - 1)

        return listOf(minX, maxX, minY, maxY)
    }

    private fun doubleToCell(): Int {
        return floor(this / this@ChunkedSpatialHash.cellSize).toInt()
    }
}