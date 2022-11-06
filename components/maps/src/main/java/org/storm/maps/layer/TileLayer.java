package org.storm.maps.layer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.storm.core.ui.Resolution;
import org.storm.maps.tile.Tile;
import org.storm.maps.tile.TileSet;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;

import java.util.function.BiConsumer;

public class TileLayer extends Layer {

  private final TileSet tileSet;

  private final int[][] skeleton;

  public TileLayer(boolean active, Resolution resolution, TileSet tileSet, int[][] skeleton) {
    super(active, resolution);
    this.tileSet = tileSet;
    this.skeleton = skeleton;

    if (active) {
      this.loadTiles();
    }
  }

  private void loadTiles() {
    skeletonForEach(this.skeleton, (r, c) -> {
      if (this.skeleton[r][c] < 0) return;

      this.addEntity(new Tile(c * (double)this.tileSet.getTileWidth(), r * (double)this.tileSet.getTileHeight(),
        this.tileSet.getTileWidth(), this.tileSet.getTileHeight()));
    });
  }

  @Override
  public void render(GraphicsContext graphicsContext, double x, double y) {
    skeletonForEach(this.skeleton, (r, c) -> {
      if (this.skeleton[r][c] < 0) return;

      Image tileImage = this.tileSet.getTileImage(this.skeleton[r][c]);

      double tx = c * tileImage.getWidth();
      double ty = r * tileImage.getHeight();

      AxisAlignedRectangle tileRect = new AxisAlignedRectangle(tx, ty, tileImage.getWidth(), tileImage.getHeight());
      AxisAlignedRectangle screenRect = new AxisAlignedRectangle(x, y, this.resolution.getWidth(), this.resolution.getHeight());

      if (tileRect.intersects(screenRect)) {
        graphicsContext.drawImage(tileImage, tx - x, ty - y);
      }
    });
  }

  private static void skeletonForEach(int[][] skeleton, BiConsumer<Integer, Integer> skeletonConsumer) {
    for (int r = 0; r < skeleton.length; r++) {
      for (int c = 0; c < skeleton[0].length; c++) {
        skeletonConsumer.accept(r, c);
      }
    }
  }

  @Override
  public void update(double time, double elapsedTime) {
    // Tile Layers do not update by default
  }
}
