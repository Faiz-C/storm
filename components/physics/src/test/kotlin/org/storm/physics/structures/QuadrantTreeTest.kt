package org.storm.physics.structures

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.storm.physics.entity.Entity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.AABB
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
    quadrantTree.insert(e)

    assertEquals(1, quadrantTree.content.size)
    assertTrue(quadrantTree.leaf)
  }

  @Test
  fun testInsert_multiple() {
    val quadrantTree = QuadrantTree(0, 100.0, 100.0)
    val totalEntities = 1000000
    var start = System.nanoTime()
    val entities: List<Entity> = (0 until totalEntities).map {
      val s: Shape = AABB(
        rand.nextInt(101).toDouble(),
        rand.nextInt(101).toDouble(),
        (rand.nextInt(25) + 1).toDouble(),
        (rand.nextInt(25) + 1).toDouble()
      )
      SimpleEntity(s, 3.0, 10.0, 0.3)
    }

    var elapsed = System.nanoTime() - start
    logger.info("Time elapsed for creating $totalEntities entities: ${elapsed / 1000000000.0}")

    start = System.nanoTime()
    entities.forEach { e -> quadrantTree.insert(e) }
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
      quadrantTree.insert(e)
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
      quadrantTree.insert(e)
      last = e
    }

    assertFalse(quadrantTree.leaf)
    assertEquals(10, quadrantTree.size)

    quadrantTree.remove(last!!)
    assertFalse(quadrantTree.leaf)
    assertEquals(9, quadrantTree.size)
  }

  @Test
  fun testClear() {
    val quadrantTree = QuadrantTree(0, 100.0, 100.0)
    val totalEntities = 10
    for (i in 0 until totalEntities) {
      val s: Shape = AABB(
        rand.nextInt(101).toDouble(),
        rand.nextInt(101).toDouble(),
        (rand.nextInt(25) + 1).toDouble(),
        (rand.nextInt(25) + 1).toDouble()
      )
      val e: Entity = SimpleEntity(s, 3.0, 10.0, 0.3)
      quadrantTree.insert(e)
    }

    assertEquals(10, quadrantTree.size)
    quadrantTree.clear()
    assertEquals(0, quadrantTree.size)
  }
}
