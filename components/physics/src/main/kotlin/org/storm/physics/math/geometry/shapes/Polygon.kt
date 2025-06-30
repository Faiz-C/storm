package org.storm.physics.math.geometry.shapes

import org.apache.commons.math3.util.FastMath
import org.apache.commons.math3.util.FastMath.max
import org.apache.commons.math3.util.FastMath.min
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Polygon
import org.storm.physics.math.Interval
import org.storm.physics.math.Vector
import org.storm.physics.math.extensions.getSquaredDistance
import org.storm.physics.math.extensions.toVector
import org.storm.physics.math.geometry.Line

/**
 * An extension of a graphical Polygon with added functionalities for calculations.
 *
 * Calculate if it's convex just calculate all interior angles are <= 180
 */
open class Polygon(
    vararg vertices: Point
) : Polygon(*vertices), CollidableShape {

    companion object {
        private const val RAY_CASTING_EPSILON = 0.00001
    }

    // e_i = <v_i, v_{i+1}> and e_V = <v, v_0> where v_i is the ith vertex of the polygon.
    val edges: List<Line> = (0 until this.vertices.size - 1)
        .map { Line(vertices[it], vertices[it + 1]) }
        .plus(Line(vertices.last(), vertices.first()))

    val isConvex: Boolean = checkConvex()

    override val center: Point = findCenter()

    /**
     * @param p point to check against
     * @return the vertex (Point) of the ConvexPolygon which is closest to p
     */
    fun getClosestVertex(p: Point): Point {
        var min = Double.POSITIVE_INFINITY
        var nearest = vertices[0]

        vertices.forEach {
            val d = p.getSquaredDistance(it)
            if (d < min) {
                min = d
                nearest = it
            }
        }

        return Point(nearest.x, nearest.y)
    }

    override fun translate(dx: Double, dy: Double) {
        this.vertices.forEach { point -> point.translate(dx, dy) }
        this.edges.forEach { edge -> edge.translate(dx, dy) }
        this.center.translate(dx, dy)
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
    override operator fun contains(p: Point): Boolean {
        val totalIntersections = edges
            // Only look for non-horizontal edges
            .filter { it.start.y != it.end.y }
            .map { castRay(p, it) }
            .fold(0) { acc, it -> acc + it }
        return totalIntersections and 1 != 0 // Check if odd
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        canvas.withSettings(canvas.settings.copy(fill = true)) {
            drawPolygon(this@Polygon.vertices)
        }
    }

    override fun toString(): String = "ConvexPolygon(vertices=$vertices)"

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
     * @param edge edge (Line) to check against
     * @return 1 iff the ray cast of the given point passes through the given edge, 0 otherwise
     */
    private fun castRay(point: Point, edge: Line): Int {
        // Get the highest and lowest points
        val (hx, hy) = if (edge.end.y > edge.start.y) edge.end else edge.start
        val (lx, ly) = if (edge.end.y > edge.start.y) edge.start else edge.end

        // Considering "on vertex" case
        val p = if (point.y == hy || point.y == ly) Point(point.x, point.y + RAY_CASTING_EPSILON) else point

        if (p.y > hy || p.y < ly || p.x > max(hx, lx)) return 0

        if (p.x < min(hx, lx)) return 1

        // dividing a double by 0 --> INFINITY
        val higherLowerSlope = (hy - ly) / (hx - lx)
        val startLowerSlope = (p.y - ly) / (p.x - lx)

        return if (startLowerSlope >= higherLowerSlope) 1 else 0
    }

    private fun checkConvex(): Boolean {
        // Check all interior angles are less than 180 degrees or pi radians
        val n = vertices.size
        for (i in 0 .. n - 1) {
            val p1 = vertices[i]
            val p2 = vertices[(i + 1) % n]
            val p3 = vertices[(i + 2) % n]

            val v1 = Vector(p2, p1)
            val v2 = Vector(p2, p3)

            val angle = FastMath.acos(v1.dot(v2) / (v1.magnitude * v2.magnitude))

            if (angle > Math.PI) {
                return false
            }
        }

        return true
    }

}
