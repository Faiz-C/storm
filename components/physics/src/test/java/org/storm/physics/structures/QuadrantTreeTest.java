package org.storm.physics.structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.storm.physics.entity.Entity;
import org.storm.physics.entity.SimpleEntity;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.math.geometry.shapes.Shape;
import org.storm.physics.math.geometry.shapes.Triangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class QuadrantTreeTest {

  private Random rand;

  @BeforeEach
  public void setup() {
    rand = new Random();
    rand.setSeed(11);
  }

  @Test
  public void testInsert_single() {
    QuadrantTree quadrantTree = new QuadrantTree(0, 100, 100);
    Shape s = new Triangle(new Point(5, 5), new Point(15, 15), new Point(7, 77));
    Entity e = new SimpleEntity(s, 3, 10, 0.3);
    quadrantTree.insert(e);

    assertEquals(1, quadrantTree.getContent().size());
    assertTrue(quadrantTree.isLeaf());
  }


  @Test
  public void testInsert_multiple() {
    QuadrantTree quadrantTree = new QuadrantTree(0, 100, 100);

    int totalEntities = 1000000;
    long start = System.nanoTime();
    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < totalEntities - 1; i++) {
      Shape s = new AxisAlignedRectangle(rand.nextInt(101), rand.nextInt(101), rand.nextInt(25) + 1, rand.nextInt(25) + 1);
      Entity e = new SimpleEntity(s, 3, 10, 0.3);
      entities.add(e);
    }
    long elapsed = System.nanoTime() - start;
    System.out.printf("Time elapsed for creating %d entities: %fs%n", totalEntities, elapsed / 1000000000.0);

    start = System.nanoTime();
    entities
      .parallelStream()
      .forEach(quadrantTree::insert);
    elapsed = System.nanoTime() - start;
    System.out.printf("Time elapsed for inserting %d entities: %fs%n", totalEntities, elapsed / 1000000000.0);

    assertFalse(quadrantTree.isLeaf());

    start = System.nanoTime();
    int size = quadrantTree.size();
    elapsed = System.nanoTime() - start;
    System.out.printf("Time elapsed for getting size for %d entities: %fs%n", totalEntities, elapsed / 1000000000.0);

    start = System.nanoTime();
    quadrantTree.clear();
    elapsed = System.nanoTime() - start;
    System.out.printf("Time elapsed for clearing tree for %d entities: %fs%n", totalEntities, elapsed / 1000000000.0);

  }

  @Test
  public void testInsert_multipleButStillLeaf() {
    QuadrantTree quadrantTree = new QuadrantTree(0, 100, 100);

    int totalEntities = 3;
    for (int i = 0; i < totalEntities - 1; i++) {
      Shape s = new AxisAlignedRectangle(rand.nextInt(101), rand.nextInt(101), rand.nextInt(25) + 1, rand.nextInt(25) + 1);
      Entity e = new SimpleEntity(s, 3, 10, 0.3);
      quadrantTree.insert(e);
    }

    assertTrue(quadrantTree.isLeaf());
  }

  @Test
  public void testRemove() {
    QuadrantTree quadrantTree = new QuadrantTree(0, 100, 100);

    int totalEntities = 10;
    Entity last = null;
    for (int i = 0; i < totalEntities; i++) {
      Shape s = new AxisAlignedRectangle(rand.nextInt(101), rand.nextInt(101), rand.nextInt(25) + 1, rand.nextInt(25) + 1);
      Entity e = new SimpleEntity(s, 3, 10, 0.3);
      quadrantTree.insert(e);
      last = e;
    }

    assertFalse(quadrantTree.isLeaf());
    assertEquals(10, quadrantTree.size());

    quadrantTree.remove(last);

    assertFalse(quadrantTree.isLeaf());
    assertEquals(9, quadrantTree.size());
  }

  @Test
  public void testClear() {
    QuadrantTree quadrantTree = new QuadrantTree(0, 100, 100);

    int totalEntities = 10;
    for (int i = 0; i < totalEntities; i++) {
      Shape s = new AxisAlignedRectangle(rand.nextInt(101), rand.nextInt(101), rand.nextInt(25) + 1, rand.nextInt(25) + 1);
      Entity e = new SimpleEntity(s, 3, 10, 0.3);
      quadrantTree.insert(e);
    }

    assertEquals(10, quadrantTree.size());

    quadrantTree.clear();
    assertEquals(0, quadrantTree.size());
  }

}
