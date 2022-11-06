package org.storm.physics.structures;

import lombok.Getter;
import org.storm.core.render.Renderable;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.transforms.UnitConvertor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A QuadrantTree is a type of SpatialDataStructure which uses a Quad Tree as its underlying data structure.
 */
public class QuadrantTree implements SpatialDataStructure {

  private static final int MAX_DEPTH = 15;
  private static final int MAX_CAPACITY = 8;

  private final int level;

  @Getter
  private final Quadrant boundary;

  private final QuadrantTree[] quadrants;

  private final Object quadrantLock;

  private final Object contentLock;

  @Getter
  private Set<Entity> content;

  @Getter
  private boolean leaf;

  public QuadrantTree(int level, Quadrant boundary) {
    this.level = level;
    this.boundary = boundary;
    this.quadrants = new QuadrantTree[4];
    this.quadrantLock = new Object();
    this.content = new HashSet<>();
    this.contentLock = new Object();
    this.leaf = true;
  }

  public QuadrantTree(int level, double width, double height) {
    this(level, new Quadrant(0, 0, width, height));
  }

  /**
   * @return size of the tree
   */
  public int size() {
    if (this.leaf) {
      synchronized (this.contentLock) {
        return this.content.size();
      }
    }

    int size = 0;
    synchronized (this.quadrantLock) {
      for (QuadrantTree quadrant : this.quadrants) {
        size += quadrant.size();
      }
    }

    synchronized (this.contentLock) {
      return size + this.content.size();
    }
  }

  @Override
  public boolean insert(Entity e) {
    if (!this.boundary.contains(e.getHurtBox())) return false;

    if (this.leaf) {
      synchronized (this.contentLock) {
        this.content.add(e);
        if (this.content.size() > MAX_CAPACITY && this.level < MAX_DEPTH) {
          this.expand();
        }
      }
      return true;
    }

    return this.getQuadrantFor(e)
      .map(q -> q.insert(e))
      .orElseGet(() -> this.content.add(e));
  }

  @Override
  public boolean remove(Entity e) {
    if (!this.boundary.contains(e.getHurtBox())) return false;

    if (this.leaf) {
      synchronized (this.contentLock) {
        return this.content.remove(e);
      }
    }
    return this.getQuadrantFor(e)
      .map(q -> q.remove(e))
      .orElse(false);
  }

  @Override
  public void clear() {
    synchronized (this.contentLock) {
      this.content.clear();
    }

    if (this.leaf) return;

    synchronized (this.quadrantLock) {
      for (int i = 0; i < this.quadrants.length; i++) {
        this.quadrants[i].clear();
        this.quadrants[i] = null;
      }
    }

    this.leaf = true;
  }

  @Override
  public Set<Entity> getCloseNeighbours(Entity e) {
    Set<Entity> neighbours = new HashSet<>(this.content);
    neighbours.remove(e);

    if (this.leaf) return neighbours;

    this.getQuadrantFor(e).ifPresent(q -> neighbours.addAll(q.getCloseNeighbours(e)));
    return neighbours;
  }

  private boolean allocate(Entity e) {
    return this.getQuadrantFor(e)
      .map(q -> q.insert(e))
      .orElse(false);
  }

  /**
   * Reallocates the contents of this QuadrantTree to its children where applicable.
   */
  private void reallocate() {
    synchronized (this.contentLock) {
      Set<Entity> newContents = new HashSet<>();
      for (Entity e : this.content) {
        if (!this.allocate(e)) newContents.add(e);
      }
      this.content = newContents;
    }
  }

  /**
   * Expands the QuadrantTree to have four children, each representing a quadrant within the space of this
   * QuadrantTree. Also reallocates the values of this parent to its children where applicable.
   */
  private void expand() {
    if (!this.leaf) return;

    synchronized (this.quadrantLock) {
      Quadrant[] quadrantBoundaries = this.boundary.subdivide();
      for (int i = 0; i < quadrantBoundaries.length; i++) {
        this.quadrants[i] = new QuadrantTree(this.level + 1, quadrantBoundaries[i]);
      }

      this.reallocate();
      this.leaf = false;
    }
  }

  /**
   * @param e Entity to check for
   * @return the QuadrantTree (child or parent) where e belongs to spatially
   */
  private Optional<QuadrantTree> getQuadrantFor(Entity e) {
    synchronized (this.quadrantLock) {
      for (QuadrantTree quadrant : this.quadrants) {
        if (quadrant.getBoundary().contains(e.getHurtBox())) return Optional.of(quadrant);
      }
    }
    return Optional.empty();
  }

  @Override
  public Renderable transform(UnitConvertor unitConvertor) {
    return (gc, x, y) -> {
      this.boundary.transform(unitConvertor).render(gc, 0, 0);
      for (QuadrantTree quadrant : this.quadrants) {
        if (quadrant != null) quadrant.transform(unitConvertor).render(gc, x, y);
      }
    };
  }

  private static class Quadrant extends AxisAlignedRectangle {

    private final double width;
    private final double height;

    public Quadrant(double x, double y, double width, double height) {
      super(x, y, width, height);
      this.width = width;
      this.height = height;
    }

    @Override
    public Renderable transform(UnitConvertor unitConvertor) {
      return (gc, x, y) -> {
        Point topLeft = this.vertices.get(TOP_LEFT_POINT);

        gc.strokeRect(x + unitConvertor.toPixels(topLeft.getX()), y + unitConvertor.toPixels(topLeft.getY()),
          unitConvertor.toPixels(this.width), unitConvertor.toPixels(this.height));
      };
    }

    public Quadrant[] subdivide() {
      Quadrant[] quadrants = new Quadrant[4];

      double halfWidth = this.width / 2;
      double halfHeight = this.height / 2;

      Point topLeft = this.vertices.get(TOP_LEFT_POINT);

      // Top Left Quadrant
      quadrants[0] = new Quadrant(topLeft.getX(), topLeft.getY(), halfWidth, halfHeight);

      // Top Right Quadrant
      quadrants[1] = new Quadrant(this.center.getX(), topLeft.getY(), halfWidth, halfHeight);

      // Bottom Right Quadrant
      quadrants[2] = new Quadrant(this.center.getX(), this.center.getY(), halfWidth, halfHeight);

      // Bottom Left Quadrant
      quadrants[3] = new Quadrant(topLeft.getX(), this.center.getY(), halfWidth, halfHeight);

      return quadrants;
    }
  }
}
