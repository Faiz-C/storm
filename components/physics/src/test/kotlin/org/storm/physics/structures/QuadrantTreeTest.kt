package org.storm.physics.structures

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.storm.core.graphics.geometry.Point
import org.storm.physics.entity.Entity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.physics.math.geometry.shapes.Shape
import org.storm.physics.math.geometry.shapes.Triangle
import java.util.*

class QuadrantTreeTest {

    companion object {
        private val logger = LoggerFactory.getLogger(QuadrantTree::class.java)
    }

    private var rand: Random = Random()

    @BeforeEach
    fun setup() {
        rand.setSeed(11)
    }

    @Test
    fun testInsert_single() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val s: Shape = Triangle(Point(5.0, 5.0), Point(15.0, 15.0), Point(7.0, 77.0))
        val e: Entity = SimpleEntity(s, 3.0, 10.0, 0.3)
        quadrantTree.insert(e, e.boundary!!)

        assertEquals(1, quadrantTree.content.size)
        assertTrue(quadrantTree.leaf)
    }

    @Test
    fun testInsert_multiple() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val maxSize = 25
        val totalEntities = 1000000
        var start = System.nanoTime()
        val entities: Set<Entity> = (0 until totalEntities).map {
            val s: Shape = AABB(
                rand.nextInt(101 - maxSize).toDouble(),
                rand.nextInt(101 - maxSize).toDouble(),
                (rand.nextInt(maxSize) + 1).toDouble(),
                (rand.nextInt(maxSize) + 1).toDouble()
            )
            SimpleEntity(s, 3.0, 10.0, 0.3)
        }.toSet()

        var elapsed = System.nanoTime() - start
        logger.info("Time elapsed for creating $totalEntities entities: ${elapsed / 1000000000.0}")

        start = System.nanoTime()
        entities.forEach { e -> assertTrue(quadrantTree.insert(e, e.boundary!!)) }
        elapsed = System.nanoTime() - start
        logger.info("Time elapsed for inserting $totalEntities entities: ${elapsed / 1000000000.0}")

        assertFalse(quadrantTree.leaf)
        start = System.nanoTime()
        val size = quadrantTree.size
        elapsed = System.nanoTime() - start
        logger.info("Time elapsed for getting $size entities: ${elapsed / 1000000000.0}")

        start = System.nanoTime()
        quadrantTree.clear()
        elapsed = System.nanoTime() - start
        logger.info("Time elapsed for clearing $totalEntities entities: ${elapsed / 1000000000.0}")
    }

    @Test
    fun testInsert_multipleButStillLeaf() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val totalEntities = 3
        for (i in 0 until totalEntities - 1) {
            val s: Shape = AABB(
                rand.nextInt(101).toDouble(),
                rand.nextInt(101).toDouble(),
                (rand.nextInt(25) + 1).toDouble(),
                (rand.nextInt(25) + 1).toDouble()
            )
            val e: Entity = SimpleEntity(s, 3.0, 10.0, 0.3)
            quadrantTree.insert(e, e.boundary!!)
        }
        assertTrue(quadrantTree.leaf)
    }

    @Test
    fun testRemove() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val totalEntities = 10
        var last: Entity? = null
        for (i in 0 until totalEntities) {
            val s: Shape = AABB(
                rand.nextInt(101).toDouble(),
                rand.nextInt(101).toDouble(),
                (rand.nextInt(25) + 1).toDouble(),
                (rand.nextInt(25) + 1).toDouble()
            )
            val e: Entity = SimpleEntity(s, 3.0, 10.0, 0.3)
            quadrantTree.insert(e, e.boundary!!)
            last = e
        }

        assertFalse(quadrantTree.leaf)
        assertEquals(10, quadrantTree.size)

        quadrantTree.remove(last!!, last.boundary!!)
        assertFalse(quadrantTree.leaf)
        assertEquals(9, quadrantTree.size)
    }

    @Test
    fun testClear() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val maxSize = 25
        val totalEntities = 10
        for (i in 0 until totalEntities) {
            val s: Shape = AABB(
                rand.nextInt(101 - maxSize).toDouble(),
                rand.nextInt(101 - maxSize).toDouble(),
                (rand.nextInt(maxSize) + 1).toDouble(),
                (rand.nextInt(maxSize) + 1).toDouble()
            )
            val e: Entity = SimpleEntity(s, 3.0, 10.0, 0.3)
            quadrantTree.insert(e, e.boundary!!)
        }

        assertEquals(10, quadrantTree.size)
        quadrantTree.clear()
        assertEquals(0, quadrantTree.size)
    }

    @Test
    fun testGetCloseNeighboursAsLeaf() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val e1 = SimpleEntity(AABB(20.0, 20.0, 30.0, 30.0), 3.0, 10.0, 0.3)
        val e2 = SimpleEntity(AABB(60.0, 65.0, 30.0, 30.0), 3.0, 10.0, 0.3)
        val e3 = SimpleEntity(Circle(45.0, 34.0, 30.0), 3.0, 10.0, 0.3)
        val e4 = SimpleEntity(Circle(15.0, 7.0, 5.0), 3.0, 10.0, 0.3)
        val e5 = SimpleEntity(Circle(50.0, 20.0, 10.0), 3.0, 10.0, 0.3)

        setOf(e1, e2, e3, e4, e5).forEach {
            assertTrue(quadrantTree.insert(it, it.boundary!!))
        }

        assertEquals(5, quadrantTree.size)

        val neighbours = quadrantTree.getCloseNeighbours(e1, e1.boundary!!)
        assertEquals(4, neighbours.size)
    }

    @Test
    fun testGetCloseNeighboursNonLeaf() {
        val quadrantTree = QuadrantTree(0, 100.0, 100.0)
        val e1 = SimpleEntity(AABB(20.0, 20.0, 30.0, 30.0), 3.0, 10.0, 0.3)
        val e2 = SimpleEntity(AABB(60.0, 65.0, 30.0, 30.0), 3.0, 10.0, 0.3)
        val e3 = SimpleEntity(Circle(45.0, 34.0, 30.0), 3.0, 10.0, 0.3)
        val e4 = SimpleEntity(Circle(15.0, 7.0, 5.0), 3.0, 10.0, 0.3)
        val e5 = SimpleEntity(Circle(50.0, 20.0, 10.0), 3.0, 10.0, 0.3)
        val e6 = SimpleEntity(Circle(40.0, 30.0, 3.0), 3.0, 10.0, 0.3)
        val e7 = SimpleEntity(Circle(55.0, 38.0, 8.0), 3.0, 10.0, 0.3)
        val e8 = SimpleEntity(Circle(35.0, 43.0, 2.0), 3.0, 10.0, 0.3)
        val e9 = SimpleEntity(Circle(82.0, 32.0, 5.0), 3.0, 10.0, 0.3)

        setOf(e1, e2, e3, e4, e5, e6, e7, e8, e9).forEach {
            assertTrue(quadrantTree.insert(it, it.boundary!!))
        }

        assertEquals(9, quadrantTree.size)

        val neighbours = quadrantTree.getCloseNeighbours(e6, e6.boundary!!)
        assertEquals(5, neighbours.size)
    }
}
