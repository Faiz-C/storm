package org.storm.physics.structures

import org.storm.core.graphics.canvas.Canvas
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.CollidableShape
import kotlin.math.floor
import kotlin.times

/**
 * A chunked spatial hash is a data structure which bridges the gap between a fixed sized spatial hash and an unbounded
 * spatial hash. Each chunk is a [chunkCells] x [chunkCells] square. All values here are in *units*.
 */
class ChunkedSpatialHash<T>(
    private val cellSize: Double,
    private val chunkCells: Int
): SpatialDataStructure<T> {

    val chunkSize = this.cellSize * this.chunkCells

    /**
     * A read only version of a Chunk that ChunkedSpatialHash stores
     */
    interface ReadOnlyChunk<T> {
        /**
         * The x coordinate of the top left corner of this Chunk
         */
        val x: Double

        /**
         * The y coordinate of the top left corner of this Chunk
         */
        val y: Double

        /**
         * The AABB that encapsulates this entire Chunk
         */
        val boundary: AABB

        /**
         * A flat list of items stored in this Chunk
         */
        val items: List<T>

        /**
         * Returns the items in the bucket in row [br] and col [bc].
         *
         * @param br the row of the bucket in the Chunk
         * @param bc the col of the bucket in the Chunk
         * @return the items in this bucket
         */
        fun bucket(br: Int, bc: Int): List<T>
    }

    internal inner class Chunk<T>(override val x: Double, override val y: Double): ReadOnlyChunk<T> {
        val buckets = Array(this@ChunkedSpatialHash.chunkCells * this@ChunkedSpatialHash.chunkCells) {
            mutableListOf<T>() // ArrayList
        }

        override val boundary: AABB = AABB(
            this.x,
            this.y,
            this@ChunkedSpatialHash.chunkSize,
            this@ChunkedSpatialHash.chunkSize
        )

        override val items: List<T> get() = this.buckets.flatMap { it }

        override fun bucket(br: Int, bc: Int): List<T> {
            val index = br * this@ChunkedSpatialHash.chunkCells + bc

            require(index < this.buckets.size) {
                "(br=$br, bc=$bc, index=$index) is not a value local coordinate for this Chunk"
            }

            return this.buckets[index]
        }

        fun clear() {
            this.buckets.forEach {
                it.clear()
            }
        }
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