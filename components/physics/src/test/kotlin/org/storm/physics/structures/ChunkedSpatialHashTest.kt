package org.storm.physics.structures

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.storm.physics.collision.Collider
import org.storm.physics.math.geometry.shapes.AABB

class ChunkedSpatialHashTest {

    private val queryCollider = Collider(AABB(0.0, 0.0, 1.0, 1.0), 3.0, 1.0)
    private val spatialHash = ChunkedSpatialHash<Collider>(cellSize = 16.0, chunkCells = 16)

    @AfterEach
    fun cleanUp() {
        spatialHash.clear()
    }

    @Test
    fun testInsertOneItem() {
        val c = Collider(AABB(x = 10.0, y = 10.0, width = 5.0, height = 5.0), 3.0, 0.3)

        val inserted = spatialHash.insert(c, c.boundary!!)
        assertTrue(inserted)

        val neighbours = spatialHash.getCloseNeighbours(queryCollider, c.boundary!!)
        assertEquals(1, neighbours.size)
        assertEquals(c, neighbours[c.boundary!!])
    }

    @Test
    fun testOverlappingInsert() {
        // Spans across cell boundary x=16.0 (touches Cell (0,0) and Cell (1,0))
        val c = Collider(AABB(x = 8.0, y = 8.0, width = 20.0, height = 5.0), 3.0, 0.3)

        spatialHash.insert(c, c.boundary!!)

        // Query point inside Cell (0,0)
        val query1 = spatialHash.getCloseNeighbours(queryCollider, AABB(10.0, 10.0, 1.0, 1.0))
        // Query point inside Cell (1,0)
        val query2 = spatialHash.getCloseNeighbours(queryCollider, AABB(20.0, 10.0, 1.0, 1.0))

        assertEquals(c, query1[c.boundary!!])
        assertEquals(c, query2[c.boundary!!])
    }

    @Test
    fun testRemoveAcrossMultipleCells() {
        val shape = AABB(x = 10.0, y = 10.0, width = 25.0, height = 25.0) // Spans 4 cells
        val c = Collider(shape, 3.0, 0.3)

        spatialHash.insert(c, shape)
        val removed = spatialHash.remove(c, shape)

        assertTrue(removed)
        val neighbours = spatialHash.getCloseNeighbours(queryCollider, shape)
        assertTrue(neighbours.isEmpty())
    }

    @Test
    fun testClearResetsByKeepsChunks() {
        val shape1 = AABB(5.0, 5.0, 2.0, 2.0)
        val shape2 = AABB(300.0, 300.0, 2.0, 2.0) // Lives in Chunk (1, 1)

        val c1 = Collider(shape1, 3.0, 0.3)
        val c2 = Collider(shape2, 3.0, 0.3)

        spatialHash.insert(c1, shape1)
        spatialHash.insert(c2, shape2)

        assertEquals(2, spatialHash.chunks.size) // 2 chunks allocated

        spatialHash.clear()

        // Chunks are retained, but buckets are empty
        assertEquals(2, spatialHash.chunks.size)
        assertTrue(spatialHash.getCloseNeighbours(c1, shape1).isEmpty())
        assertTrue(spatialHash.getCloseNeighbours(c2, shape2).isEmpty())
    }

    @Test
    fun testNegativeWorldCoordinatesMapCorrectly() {
        val shape = AABB(x = -30.0, y = -30.0, width = 5.0, height = 5.0)
        val c = Collider(shape, 3.0, 0.3)

        spatialHash.insert(c, shape)

        val neighbours = spatialHash.getCloseNeighbours(queryCollider, shape)
        assertEquals(1, neighbours.size)
        assertEquals(c, neighbours[shape])
    }

    @Test
    fun testGetNeighboursIgnoresSelf() {
        val playerShape = AABB(x = 2.0, y = 2.0, width = 4.0, height = 4.0)
        val player = Collider(playerShape, 3.0, 0.3)

        val enemyShape = AABB(x = 8.0, y = 2.0, width = 4.0, height = 4.0)
        val enemy = Collider(enemyShape, 3.0, 0.3)

        // Insert player twice with two separate shapes into the same region
        spatialHash.insert(player, playerShape)
        spatialHash.insert(enemy, enemyShape)

        // Query using player's headShape
        val neighbours = spatialHash.getCloseNeighbours(player, playerShape)

        // Should contain the enemy, but NOT the player's head or body
        assertEquals(1, neighbours.size)
        assertEquals(enemy, neighbours[enemyShape])
        assertFalse(neighbours.containsValue(player))
    }
}