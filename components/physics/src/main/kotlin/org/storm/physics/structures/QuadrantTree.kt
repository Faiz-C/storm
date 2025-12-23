package org.storm.physics.structures

import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.shape.Rectangle.Companion.TOP_LEFT_POINT
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.CollidableShape

/**
 * A QuadrantTree is a type of SpatialDataStructure which uses a Quad Tree as its underlying data structure.
 */
class QuadrantTree(
    private val level: Int,
    private val boundary: Quadrant
) : SpatialDataStructure {

    companion object {
        private const val MAX_DEPTH = 15
        private const val MAX_CAPACITY = 8

        class Quadrant(
            x: Double,
            y: Double,
            width: Double,
            height: Double
        ) : AABB(x, y, width, height) {

            override suspend fun render(canvas: Canvas, x: Double, y: Double) {
                canvas.drawPolygonWithUnits(this.vertices)
            }

            fun subdivide(): Array<Quadrant> {
                val halfWidth = this.width / 2
                val halfHeight = this.height / 2
                val (x, y) = this.vertices[TOP_LEFT_POINT]

                return arrayOf(
                    // Top Left Quadrant
                    Quadrant(x, y, halfWidth, halfHeight),

                    // Top Right Quadrant
                    Quadrant(this.center.x, y, halfWidth, halfHeight),

                    // Bottom Right Quadrant
                    Quadrant(this.center.x, this.center.y, halfWidth, halfHeight),

                    // Bottom Left Quadrant
                    Quadrant(x, this.center.y, halfWidth, halfHeight)
                )
            }
        }
    }

    private val quadrants: Array<QuadrantTree?> = arrayOfNulls(4)
    private val quadrantLock: Any = Any()
    private val contentLock: Any = Any()

    var content: MutableMap<CollidableShape, Collider> = mutableMapOf()
        private set

    var leaf = true
        private set

    constructor(level: Int, width: Double, height: Double) : this(level, Quadrant(0.0, 0.0, width, height))

    /**
     * @return size of the tree
     */
    val size: Int
        get() {
            if (this.leaf) {
                return synchronized(this.contentLock) { this@QuadrantTree.content.size }
            }

            val size = synchronized(this.quadrantLock) {
                this.quadrants.fold(0) { acc, it -> acc + it!!.size }
            }

            return synchronized(this.contentLock) { size + this@QuadrantTree.content.size }
        }

    override fun insert(collider: Collider, boundary: CollidableShape): Boolean {
        if (!this.boundary.contains(boundary)) return false

        return if (this.leaf) {
            synchronized(this.contentLock) {
                this.content[boundary] = collider
                if (this.content.size > MAX_CAPACITY && this.level < MAX_DEPTH) {
                    this.expand()
                }
            }
            true
        } else {
            this.getQuadrantFor(boundary)?.insert(collider, boundary) ?: run {
                this.content[boundary] = collider
                true
            }
        }
    }

    override fun remove(collider: Collider, boundary: CollidableShape): Boolean {
        if (!this.boundary.contains(boundary)) return false

        return if (this.leaf) {
            synchronized(this.contentLock) {
                this.content.remove(boundary)
                true
            }
        } else {
            this.getQuadrantFor(boundary)?.remove(collider, boundary)
                ?: synchronized(this.contentLock) {
                    // This handles the case where the boundary might exist in between quadrants
                    this.content.remove(boundary) != null
                }
        }
    }

    override fun clear() {
        synchronized(this.contentLock) { this.content.clear() }

        if (this.leaf) return

        synchronized(this.quadrantLock) {
            for (i in this.quadrants.indices) {
                this.quadrants[i]!!.clear()
                this.quadrants[i] = null
            }
        }

        this.leaf = true
    }

    override fun getCloseNeighbours(collider: Collider, boundary: CollidableShape): Map<CollidableShape, Collider> {
        val neighbours = this.content.filterKeys {
            !collider.boundaries.containsValue(it)
        }

        return if (this.leaf) {
            neighbours
        } else {
            this.getQuadrantFor(boundary)?.let {
                neighbours.plus(it.getCloseNeighbours(collider, boundary))
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
     * Allocates (inserts) the Shape for the given Collider into the correct quadrant in the tree.
     *
     * @param collider Collider for which the boundary belongs too
     * @param boundary boundary Shape to allocate
     */
    private fun allocate(collider: Collider, boundary: CollidableShape): Boolean {
        return this.getQuadrantFor(boundary)?.insert(collider, boundary) ?: false
    }

    /**
     * Reallocates the contents of this QuadrantTree to its children where applicable.
     */
    private fun reallocate() {
        this.content = synchronized(this.contentLock) {
            // The new content for this tree are all the Entities which couldn't be allocated
            this.content.filter { (s, e) ->
                !this.allocate(e, s)
            }.toMutableMap()
        }
    }

    /**
     * Expands the QuadrantTree to have four children, each representing a quadrant within the space of this
     * QuadrantTree. Also reallocates the values of this parent to its children where applicable.
     */
    private fun expand() {
        if (!this.leaf) return

        synchronized(this.quadrantLock) {
            val quadrantBoundaries = this.boundary.subdivide()
            for (i in quadrants.indices) {
                quadrants[i] = QuadrantTree(level + 1, quadrantBoundaries[i])
            }

            this.reallocate()
            leaf = false
        }

    }

    /**
     * @param boundary boundary Shape to check for
     * @return the QuadrantTree (child or parent) where s belongs to spatially, null if it belongs to no one
     */
    private fun getQuadrantFor(boundary: CollidableShape): QuadrantTree? {
        return synchronized(this.quadrantLock) {
            this.quadrants.firstOrNull { it?.boundary?.contains(boundary) == true }
        }
    }

}