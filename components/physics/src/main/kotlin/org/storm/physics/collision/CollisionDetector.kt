package org.storm.physics.collision

import org.apache.commons.math3.util.FastMath
import org.storm.physics.math.Vector
import org.storm.physics.math.extensions.getDistance
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.physics.math.geometry.shapes.ConvexPolygon
import org.storm.physics.math.geometry.shapes.Shape

/**
 * The CollisionDetector is useful for checking whether two given closed shapes (in 2D) have collided. It also has the ability
 * to determine the Minimum Translation Vector to allow for reactions upon colliding.
 *
 * Currently Supported:
 *
 * Convex Polygon x Convex Polygon
 * Circle x Convex Polygon
 * Circle x Circle
 */
object CollisionDetector {

    /**
     * @return true if the two shapes collide, false otherwise
     */
    @JvmStatic
    fun check(s1: Shape, s2: Shape): Boolean {
        return checkMtv(s1, s2) !== Vector.ZERO_VECTOR
    }

    /**
     * @return the Minimum Translation Vector (Zero Vector if no collision) calculated from the collision
     */
    @JvmStatic
    fun checkMtv(s1: Shape, s2: Shape): Vector {
        // If both circles, need to treat it differently
        return if (s1.edges.isEmpty() && s2.edges.isEmpty()) calcCircleMtv(s1 as Circle, s2 as Circle) else calcMtv(
            s1,
            s2
        )
    }

    /**
     * Checks if the given shapes have collided and returns a Force representing the minimum translation vector.
     *
     * Collision Detection is handled via the Separation Axis Theorem. This algorithm works to cover the cases of
     * ConvexPolygon + ConvexPolygon and Circle + ConvexPolygon but not Circle + Circle
     *
     * @param s1 shape to check
     * @param s2 shape to check
     * @return a Vector representing the MTV if the two polygons have collided, ZERO_VECTOR otherwise
     */
    private fun calcMtv(s1: Shape, s2: Shape): Vector {
        var dv = Vector.ZERO_VECTOR
        var mag = Double.POSITIVE_INFINITY

        axes(s1, s2).forEach {
            val projection1 = s1.project(it)
            val projection2 = s2.project(it)
            val overlap = projection1.getOverlap(projection2)
            if (overlap <= 0) {
                return Vector.ZERO_VECTOR
            } else if (overlap < mag) {
                mag = overlap
                dv = it
            }
        }

        // Need to orient to face towards Shape 1 always

        // 1) Get the direction vector between the center of the shapes (s2.center - s1.center)
        // 2) Calculate the dot product between the vector found in (1) and the mtv
        // 3) If the dot product is positive the two are facing in the same direction, so we have to flip the mtv
        //    in order for it to be facing p1
        //
        // *NOTE* to keep things simple we always want to point our mtv towards s1
        val s1ToS2 = Vector(s1.center, s2.center)
        return if (dv.dot(s1ToS2) > 0) dv.flip().scaleToMagnitude(mag) else dv.scaleToMagnitude(mag)
    }

    /**
     * Checks if the given circles collide and returns the minimum translation vector to allow for separation.
     *
     * @param c1 circle to check
     * @param c2 circle to check
     * @return a Vector representing the MTV if the two circles have collided, ZERO_VECTOR otherwise
     */
    private fun calcCircleMtv(c1: Circle, c2: Circle): Vector {
        val distanceBetweenCenters = c2.center.getDistance(c1.center)
        val radiusSum = c1.radius + c2.radius
        if (distanceBetweenCenters >= radiusSum) return Vector.ZERO_VECTOR
        val mtvAngle = FastMath.atan2(
            c2.center.y - c1.center.y,
            c2.center.x - c1.center.x
        )
        val overlap = radiusSum - distanceBetweenCenters
        return Vector(-FastMath.cos(mtvAngle) * overlap, -FastMath.sin(mtvAngle) * overlap)
    }

    /**
     * @param s1 shape to get axes for
     * @param s2 shape to get axes for
     * @return a set of Vectors representing the separating axes to check
     */
    private fun axes(s1: Shape, s2: Shape): Set<Vector> {
        val axes: MutableSet<Vector> = HashSet()

        s1.edges.forEach {
            axes.add(it.vectorFromStart.counterClockwiseNormal.normalized)
        }

        s2.edges.forEach {
            axes.add(it.vectorFromStart.counterClockwiseNormal.normalized)
        }

        // Special Case for Circles, need to include a special axis
        if (s1.edges.isEmpty()) {
            axes.add(
                Vector(
                    s1.center,
                    (s2 as ConvexPolygon).getClosestVertex(s1.center)
                ).counterClockwiseNormal.normalized
            )
        } else if (s2.edges.isEmpty()) {
            axes.add(
                Vector(
                    s2.center,
                    (s1 as ConvexPolygon).getClosestVertex(s2.center)
                ).counterClockwiseNormal.normalized
            )
        }

        return axes
    }
}
