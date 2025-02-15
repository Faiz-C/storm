package org.storm.physics.math.geometry.shapes

import org.apache.commons.math3.util.FastMath
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Settings
import org.storm.core.render.geometry.Point
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.extensions.getSquaredDistance
import org.storm.physics.math.extensions.rotate
import org.storm.physics.math.extensions.toVector
import org.storm.physics.math.geometry.LineSegment

// TODO: None of these should be open, they should all be data classes and used via composition instead of inheritance

/**
 * A ConvexPolygon represents a mathematical convex polygon with N vertices in 2D space. Vertices are supplied in clockwise
 * order and edges are generated by following such order (i.e. (v_i, v_i+1) is an edge)
 *
 * Potentially might want to update the internal implementation here to use a Circularly Doubly Linked List for the vertices
 * and edges.
 */
open class ConvexPolygon(
    vararg vertices: Point
) : Shape {

    companion object {
        private const val RAY_CASTING_EPSILON = 0.00001
    }

    // Order and uniqueness matters for vertices
    val vertices: List<Point> = vertices.distinct()

    override val edges: List<LineSegment> = findEdges()

    override val center: Point = findCenter()

    init {
        require(this.vertices.size > 2) { "a polygon must have a minimum of 3 distinct vertices" }
    }

    /**
     * @param p point to check against
     * @return the vertex (Point) of the ConvexPolygon which is closest to p
     */
    fun getClosestVertex(p: Point): Point {
        var min = Double.POSITIVE_INFINITY
        var nearest: Point = vertices[0]

        vertices.forEach {
            val d = p.getSquaredDistance(it)
            if (d < min) {
                min = d
                nearest = it
            }
        }

        return nearest
    }

    override fun translate(dx: Double, dy: Double) {
        this.vertices.forEach { point -> point.translate(dx, dy) }
        this.edges.forEach { edge -> edge.translate(dx, dy) }
        this.center.translate(dx, dy)
    }

    override fun rotate(point: Point, angle: Double) {
        this.vertices.forEach {
            it.rotate(point, angle)
        }

        this.center.rotate(point, angle)
    }

    /**
     * Returns the projection of this Polygon onto the given axis
     *
     * The projection of a Polygon on a given axis is the 1d line segment (or interval)
     * [A, B] where A = min dot product between the axis and the vertices of the polygon
     * and B = max dot product between the axis and the vertices of the polygon
     *
     * @param axis the vector axis to project onto
     * @return Interval representing the projection
     */
    override fun project(axis: Vector): Interval {
        var min = Double.POSITIVE_INFINITY
        var max = 0.0

        vertices.forEach {
            val d = axis.dot(it.toVector())
            if (d < min) min = d
            if (d > max) max = d
        }

        return Interval(min, max)
    }

    /**
     * An implementation of contains for a generic polygon using the Ray Casting Algorithm
     *
     * @param p Point to check
     * @return true if p is contained within the convex polygon
     */
    override fun contains(p: Point): Boolean {
        val totalIntersections = edges
            // Only look for non-horizontal edges
            .filter { it.start.y != it.end.y }
            .map { castRay(p, it) }
            .fold(0) { acc, it -> acc + it }
        return totalIntersections and 1 != 0 // Check if odd
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(fill = true)) {
            it.drawPolygon(this.vertices)
        }
    }

    override fun toString(): String = "ConvexPolygon(vertices=$vertices)"

    /**
     * Returns the edges of the polygon as vectors. The edges are defined as such:
     *
     * e_i = <v_i, v_{i+1}> and e_V = <v, v_0> where v_i is the ith vertex of the polygon.
     *
     * @return the edges of the polygon
     */
    private fun findEdges(): List<LineSegment> {
        return (0 until vertices.size - 1).map { i ->
            LineSegment(vertices[i], vertices[i + 1])
        }.plus(LineSegment(vertices[vertices.size - 1], vertices[0]))
    }

    /**
     * @return calculates and returns the center of a generic ConvexPolygon
     */
    private fun findCenter(): Point {
        var xNumerator = 0.0
        var yNumerator = 0.0
        var denominator = 0.0
        val vertexCount = vertices.size

        vertices.forEachIndexed { i, vertex ->
            val (x, y) = vertex

            // Neighboring Vertex
            val (nx, ny) = if (i == vertexCount - 1) vertices[0] else vertices[i + 1]

            // Calculate the determinant of the follow 2x2 matrix
            // [a b] = [x   y]
            // [c d] = [nx ny]
            // --> ad - bc = x * ny - y * nx
            val det = x * ny - y * nx
            xNumerator += (x + nx) * det
            yNumerator += (y + ny) * det
            denominator += det
        }

        return Point(xNumerator / (3 * denominator), yNumerator / (3 * denominator))
    }

    /**
     * @param point Point to ray cast
     * @param edge edge (LineSegment) to check against
     * @return 1 iff the ray cast of the given point passes through the given edge, 0 otherwise
     */
    private fun castRay(point: Point, edge: LineSegment): Int {
        // Get the highest and lowest points
        val (hx, hy) = if (edge.end.y > edge.start.y) edge.end else edge.start
        val (lx, ly) = if (edge.end.y > edge.start.y) edge.start else edge.end

        // Considering "on vertex" case
        val p = if (point.y == hy || point.y == ly) Point(point.x, point.y + RAY_CASTING_EPSILON) else point

        if (p.y > hy || p.y < ly || p.x > FastMath.max(hx, lx)) return 0

        if (p.x < FastMath.min(hx, lx)) return 1

        // dividing a double by 0 --> INFINITY
        val higherLowerSlope = (hy - ly) / (hx - lx)
        val startLowerSlope = (p.y - ly) / (p.x - lx)

        return if (startLowerSlope >= higherLowerSlope) 1 else 0
    }

}
