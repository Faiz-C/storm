package org.storm.physics.math.geometry.shapes

import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle.Companion.BOTTOM_LEFT_POINT
import org.storm.core.graphics.geometry.shape.Rectangle.Companion.BOTTOM_RIGHT_POINT
import org.storm.core.graphics.geometry.shape.Rectangle.Companion.TOP_LEFT_POINT
import org.storm.core.graphics.geometry.shape.Rectangle.Companion.TOP_RIGHT_POINT
import org.storm.physics.math.extensions.getSquaredDistance

/**
 * An Axis Aligned Bounding Box (Rectangle) is a Polygon with four sides that is aligned with the x-axis and y-axis
 * accordingly.
 */
open class AABB(
    x: Double,
    y: Double,
    val width: Double,
    val height: Double
): Polygon(
    Point(x, y),
    Point(x + width, y),
    Point(x + width, y + height),
    Point(x, y + height)
) {

    val x: Double get() = vertices[TOP_LEFT_POINT].x

    val y: Double get() = vertices[TOP_LEFT_POINT].y

    /**
     * @param aabb AxisAlignedRectangle to check against
     * @return true if this and the given AxisAlignedRectangle intersect
     */
    fun intersects(aabb: AABB): Boolean {
        val (blx, bly) = this.vertices[BOTTOM_LEFT_POINT]
        val (oblx, obly) = aabb.vertices[BOTTOM_LEFT_POINT]
        val (trpx, trpy) = this.vertices[TOP_RIGHT_POINT]
        val (otrx, otry) = aabb.vertices[TOP_RIGHT_POINT]
        return otrx >= blx && oblx <= trpx && otry <= bly && obly >= trpy
    }

    /**
     * @param p Point to check
     * @return true iff the point is strictly contained within this rectangle (not on boundaries)
     */
    fun strictlyContains(p: Point): Boolean {
        // Axis Aligned Rectangles have a quicker way to check for point containment
        val (tlx, tly) = this.vertices[TOP_LEFT_POINT]
        val (trx, _) = this.vertices[TOP_RIGHT_POINT]
        val (_, bry) = this.vertices[BOTTOM_RIGHT_POINT]

        return p.x > tlx && p.x < trx && p.y > tly && p.y < bry
    }

    /**
     * @param shape Shape to check
     * @return true iff the given Shape is completely contained within this rectangle
     */
    operator fun contains(shape: CollidableShape): Boolean {
        return if (shape is Circle) {
            containsCircle(shape)
        } else {
            containsPolygon(shape as Polygon)
        }
    }

    override operator fun contains(p: Point): Boolean {
        // Axis Aligned Rectangles have a quicker way to check for point containment
        val (tlx, tly) = this.vertices[TOP_LEFT_POINT]
        val (trx, _) = this.vertices[TOP_RIGHT_POINT]
        val (_, bry) = this.vertices[BOTTOM_RIGHT_POINT]

        return p.x >= tlx && p.x <= trx && p.y >= tly && p.y <= bry
    }

    /**
     * @param circle Circle to check
     * @return true if the given Circle is completely contained within this rectangle
     */
    private fun containsCircle(circle: Circle): Boolean {
        val nearestVertex = getClosestVertex(circle.center)
        return this.contains(circle.center) && nearestVertex.getSquaredDistance(circle.center) > circle.radius * circle.radius
    }

    /**
     * @param polygon Polygon to check
     * @return true if the given Shape is completely contained within this rectangle
     */
    private fun containsPolygon(polygon: Polygon): Boolean {
        polygon.vertices.forEach { vertex ->
            if (!this.contains(vertex)) {
                return false
            }
        }
        return true
    }
}
