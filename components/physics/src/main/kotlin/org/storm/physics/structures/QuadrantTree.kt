package org.storm.physics.structures

import org.storm.core.render.Renderable
import org.storm.physics.entity.Entity
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.physics.math.geometry.shapes.Shape
import org.storm.physics.transforms.UnitConvertor

/**
 * A QuadrantTree is a type of SpatialDataStructure which uses a Quad Tree as its underlying data structure. This tree
 * was modified to support multisection boundaries.
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
      private val width: Double,
      private val height: Double
    ) : AABB(x, y, width, height) {

      override fun transform(unitConvertor: UnitConvertor) = Renderable { gc, x, y ->
        val (x1, y1) = this.vertices[TOP_LEFT_POINT]
        gc.strokeRect(
          x + unitConvertor.toPixels(x1),
          y + unitConvertor.toPixels(y1),
          unitConvertor.toPixels(width),
          unitConvertor.toPixels(height)
        )
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

  var content: MutableMap<Shape, Entity> = mutableMapOf()
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

  override fun insert(e: Entity, boundarySection: Shape): Boolean {
    if (!this.boundary.contains(boundarySection)) return false

    return if (this.leaf) {
      synchronized(this.contentLock) {
        this.content[boundarySection] = e
        if (this.content.size > MAX_CAPACITY && this.level < MAX_DEPTH) {
          this.expand()
        }
      }
      true
    } else {
      this.getQuadrantFor(boundarySection)?.insert(e, boundarySection) ?: run {
        this.content[boundarySection] = e
        true
      }
    }
  }

  override fun remove(e: Entity, boundarySection: Shape): Boolean {
    if (!this.boundary.contains(boundarySection)) return false

    return if (this.leaf) {
      synchronized(this.contentLock) {
        this.content.remove(boundarySection)
        true
      }
    } else {
      this.getQuadrantFor(boundarySection)?.remove(e, boundarySection) ?: false
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

  override fun getCloseNeighbours(e: Entity, boundarySection: Shape): Map<Shape, Entity> {
    val neighbours = this.content.filterKeys {
      !e.boundaries.containsValue(it)
    }

    return if (this.leaf) {
      neighbours
    } else {
      this.getQuadrantFor(boundarySection)?.let {
        neighbours.plus(it.getCloseNeighbours(e, boundarySection))
      } ?: neighbours
    }
  }

  /**
   * Allocates (inserts) the given Entity into the correct quadrant in the tree
   * @param e Entity to allocate
   */
  private fun allocate(e: Entity, s: Shape): Boolean {
    return this.getQuadrantFor(s)?.insert(e, s) ?: false
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
   * @param e Entity to check for
   * @return the QuadrantTree (child or parent) where e belongs to spatially, null if it belongs to no one
   */
  private fun getQuadrantFor(s: Shape): QuadrantTree? {
    return synchronized(this.quadrantLock) {
      this.quadrants.firstOrNull { it?.boundary?.contains(s) == true }
    }
  }

  override fun transform(unitConvertor: UnitConvertor): Renderable = Renderable { gc, x, y ->
    this.boundary.transform(unitConvertor).render(gc, 0.0, 0.0)
    this.quadrants.forEach {
      it?.transform(unitConvertor)?.render(gc, x, y)
    }
  }

}
