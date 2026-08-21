package org.storm.physics.structures

import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.shape.Rectangle.Companion.TOP_LEFT_POINT
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * A QuadrantTree is a type of SpatialDataStructure which uses a Quad Tree as its underlying data structure.
 */
class QuadrantTree<T>(
    private val level: Int,
    private val boundary: Quadrant
) : SpatialDataStructure<T> {

    private companion object {
        const val MAX_DEPTH = 15
        const val MAX_CAPACITY = 8
    }

    class Quadrant(
        x: Double,
        y: Double,
        width: Double,
        height: Double
    ): Renderable {

        private val aabb = AABB(x, y, width, height)

        override suspend fun render(canvas: Canvas, x: Double, y: Double) {
            canvas.drawPolygonWithUnits(this.aabb.vertices)
        }

        fun contains(boundary: CollidableShape): Boolean {
            return this.aabb.contains(boundary)
        }

        fun subdivide(): Array<Quadrant> {
            val halfWidth = this.aabb.width / 2
            val halfHeight = this.aabb.height / 2
            val (x, y) = this.aabb.vertices[TOP_LEFT_POINT]

            return arrayOf(
                // Top Left Quadrant
                Quadrant(x, y, halfWidth, halfHeight),

                // Top Right Quadrant
                Quadrant(this.aabb.center.x, y, halfWidth, halfHeight),

                // Bottom Right Quadrant
                Quadrant(this.aabb.center.x, this.aabb.center.y, halfWidth, halfHeight),

                // Bottom Left Quadrant
                Quadrant(x, this.aabb.center.y, halfWidth, halfHeight)
            )
        }
    }

    private val quadrants: Array<QuadrantTree<T>?> = arrayOfNulls(4)

    var content: MutableMap<CollidableShape, T> = mutableMapOf()
        private set

    var leaf = true
        private set

    constructor(level: Int, width: Double, height: Double) : this(level, Quadrant(0.0, 0.0, width, height))

    /**
     * @return size of the tree
     */
    val size: Int
        get() {
            if (this.leaf) return this.content.size

            val size = this.quadrants.fold(0) { acc, it -> acc + it!!.size }

            return size + this@QuadrantTree.content.size
        }

    override fun insert(item: T, boundary: CollidableShape): Boolean {
        return this.boundary.contains(boundary) && if (this.leaf) {
            this.content[boundary] = item
            if (this.content.size > MAX_CAPACITY && this.level < MAX_DEPTH) {
                this.expand()
            }
            true
        } else {
            this.getQuadrantFor(boundary)?.insert(item, boundary) ?: run {
                this.content[boundary] = item
                true
            }
        }
    }

    override fun remove(item: T, boundary: CollidableShape): Boolean {
        return this.boundary.contains(boundary) && if (this.leaf) {
            this.content.remove(boundary)
            true
        } else {
            this.getQuadrantFor(boundary)?.remove(item, boundary)
                // This handles the case where the boundary might exist in between quadrants
                ?: (this.content.remove(boundary) != null)
        }
    }

    override fun clear() {
        this.content.clear()

        if (this.leaf) return

        for (i in this.quadrants.indices) {
            this.quadrants[i]!!.clear()
            this.quadrants[i] = null
        }

        this.leaf = true
    }

    override fun getCloseNeighbours(item: T, boundary: CollidableShape): Map<CollidableShape, T> {
        // Items are not restricted to just one boundary so when getting neighbours we want to ensure the item itself
        // doesn't get included if we happen to be near another one of its boundaries.
        val neighbours = this.content.filter { (_, relatedItem) ->
            item != relatedItem
        }

        return if (this.leaf) {
            neighbours
        } else {
            this.getQuadrantFor(boundary)?.let {
                neighbours.plus(it.getCloseNeighbours(item, boundary))
            } ?: neighbours
        }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        this.boundary.render(canvas, 0.0, 0.0)
        this.quadrants.forEach {
            it?.render(canvas, x, y)
        }
    }

    /**
     * Allocates (inserts) the Shape for the given Item into the correct quadrant in the tree.
     *
     * @param item Item for which the boundary belongs too
     * @param boundary boundary Shape to allocate
     */
    private fun allocate(item: T, boundary: CollidableShape): Boolean {
        return this.getQuadrantFor(boundary)?.insert(item, boundary) ?: false
    }

    /**
     * Reallocates the contents of this QuadrantTree to its children where applicable.
     */
    private fun reallocate() {
        // The new content for this tree are all the Entities which couldn't be allocated
        this.content = this.content.filter { (s, e) ->
            !this.allocate(e, s)
        }.toMutableMap()
    }

    /**
     * Expands the QuadrantTree to have four children, each representing a quadrant within the space of this
     * QuadrantTree. Also reallocates the values of this parent to its children where applicable.
     */
    private fun expand() {
        if (!this.leaf) return

        val quadrantBoundaries = this.boundary.subdivide()
        for (i in quadrants.indices) {
            quadrants[i] = QuadrantTree(level + 1, quadrantBoundaries[i])
        }

        this.reallocate()
        leaf = false

    }

    /**
     * @param boundary boundary Shape to check for
     * @return the QuadrantTree (child or parent) where s belongs to spatially, null if it belongs to no one
     */
    private fun getQuadrantFor(boundary: CollidableShape): QuadrantTree<T>? {
        return this.quadrants.firstOrNull { it?.boundary?.contains(boundary) == true }
    }

}