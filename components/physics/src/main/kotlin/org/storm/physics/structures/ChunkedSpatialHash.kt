package org.storm.physics.structures

import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.canvas.Color
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

    // Needs to be up here so the inner class below can use it
    val chunkSize = this.cellSize * this.chunkCells

    /**
     * A read only version of a Chunk that ChunkedSpatialHash stores
     */
    interface ReadOnlyChunk<T> {
        /**
         * The row in the spatial hash this belongs too
         */
        val row: Int

        /**
         * The col in the spatial hash this belongs too
         */
        val col: Int

        /**
         * The AABB that encapsulates this entire Chunk
         */
        val boundary: AABB

        /**
         * A flat list of items stored in this Chunk
         */
        val items: List<Entry<T>>

        /**
         * Returns the items in the bucket in row [br] and col [bc].
         *
         * @param br the row of the bucket in the Chunk
         * @param bc the col of the bucket in the Chunk
         * @return the items in this bucket
         */
        fun items(br: Int, bc: Int): List<Entry<T>>
    }

    /**
     * A simple Entry to hold both an item, and it's collidable shape.
     */
    data class Entry<T>(val item: T, val shape: CollidableShape)

    /**
     * A Chunk is a square of space on the screen split up evenly into square buckets. Each bucket stores any number
     * of items.
     */
    private inner class Chunk<T>(override val row: Int, override val col: Int): ReadOnlyChunk<T> {
        val buckets = Array(this@ChunkedSpatialHash.chunkCells * this@ChunkedSpatialHash.chunkCells) {
            mutableListOf<Entry<T>>() // ArrayList
        }

        override val boundary: AABB = AABB(
            this.col * this@ChunkedSpatialHash.chunkSize,
            this.row * this@ChunkedSpatialHash.chunkSize,
            this@ChunkedSpatialHash.chunkSize,
            this@ChunkedSpatialHash.chunkSize
        )

        override val items: List<Entry<T>> get() = this.buckets.flatMap { it }

        fun clear() {
            // While loop > for loop > forEach done particularly for performance
            var i = 0
            val len = this.buckets.size
            while (i < len) {
                this.buckets[i].clear()
                i++
            }
        }

        fun bucket(br: Int, bc: Int): MutableList<Entry<T>> {
            val index = br * this@ChunkedSpatialHash.chunkCells + bc

            require(index < this.buckets.size) {
                "(br=$br, bc=$bc, index=$index) is not a value local coordinate for this Chunk"
            }

            return this.buckets[index]
        }

        override fun items(br: Int, bc: Int): List<Entry<T>> {
            return bucket(br, bc)
        }
    }

    val chunks: Map<Long, ReadOnlyChunk<T>>
        field = mutableMapOf<Long, Chunk<T>>()

    override fun insert(
        item: T,
        boundary: CollidableShape
    ): Boolean {
        var added = false
        val entry = Entry(item, boundary)
        forEachCell(boundary.aabbBounds(), true) { chunk, br, bc ->
            if (chunk.bucket(br, bc).add(entry)) {
                added = true
            }
        }

        return added
    }

    override fun remove(
        item: T,
        boundary: CollidableShape
    ): Boolean {
        var removed = false
        val entry = Entry(item, boundary)
        forEachCell(boundary.aabbBounds(), false) { chunk, br, bc ->
            if (chunk.bucket(br, bc).remove(entry)) {
                removed = true
            }
        }

        return removed
    }

    override fun clear() {
        for (chunk in this.chunks.values) {
            chunk.clear()
        }
    }

    override fun getCloseNeighbours(
        item: T,
        boundary: CollidableShape
    ): Map<CollidableShape, T> {
        val results = mutableMapOf<CollidableShape, T>()

        forEachCell(boundary.aabbBounds(), false) { chunk, br, bc ->
            for ((storedItem, shape) in chunk.bucket(br, bc)) {
                if (storedItem != item) {
                    results[shape] = storedItem
                }
            }
        }

        return results
    }

    /**
     * For debugging only, we just draw the bounds of each chunk
     */
    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        val color = Color(255.0, 0.0, 0.0, 0.0)
        canvas.withSettings(color = color) {
            this@ChunkedSpatialHash.chunks.values.forEach { chunk ->
                chunk.boundary.render(canvas, x, y)
            }
        }
    }

    private inline fun forEachCell(
        aabb: AABB,
        createIfMissing: Boolean,
        action: (chunk: Chunk<T>, br: Int, bc: Int) -> Unit
    ) {
        val minCellCol = floor(aabb.x / this.cellSize).toInt()
        val minCellRow = floor(aabb.y / this.cellSize).toInt()
        val maxCellCol = floor((aabb.x + aabb.width) / this.cellSize).toInt()
        val maxCellRow = floor((aabb.y + aabb.height) / this.cellSize).toInt()

        // Below we do the following:
        // 1) Loop through the rows [minCellRow, maxCellRow] followed by the cols [minCellCol, maxCellCol]
        // 2) While looping calculate the chunk row (cr), chunk col (cc), bucket row (br), bucket col (bc)
        // 3) We then look up (or create) the chunk to perform [action] on it.
        var row = minCellRow
        while (row <= maxCellRow) {
            val cr = floor(row.toDouble() / this.chunkCells).toInt()
            val br = row - (cr * this.chunkCells)

            var col = minCellCol
            while (col <= maxCellCol) {
                val cc = floor(col.toDouble() / this.chunkCells).toInt()
                val bc = col - (cc * this.chunkCells)

                val key = createKey(cr, cc)

                if (createIfMissing) {
                    this.chunks.getOrPut(key) {
                        Chunk(cr, cc)
                    }
                } else {
                    this.chunks[key]
                }?.let {
                    action(it, br, bc)
                }

                col++
            }

            row++
        }
    }

    /**
     * Creates a key by packing both the bucket row [br] and the bucket col [bc] into a single 64 bit long. This
     * works because each integer value is 32 bit.
     *
     * @param br the bucket row
     * @param bc the bucket col
     * @return a single Long which packs the two together
     */
    private fun createKey(br: Int, bc: Int): Long {
        return (br.toLong() shl 32) or (bc.toLong() and 0xFFFFFFFFL)
    }
}