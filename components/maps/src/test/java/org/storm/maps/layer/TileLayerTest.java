package org.storm.maps.layer;

import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.storm.core.ui.Resolutions;
import org.storm.core.ui.Window;
import org.storm.maps.tile.TileSet;
import org.storm.physics.math.geometry.Point;

public class TileLayerTest extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Window window = new Window(Resolutions.SD);

    TileSet tileSet = new TileSet("src/test/resources/tiles/testTileSet.png", 32, 32);
    int[][] skeleton = new int[][] {
      new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
      new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
      new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
      new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
      new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
      new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
    };

    Layer layer = new TileLayer(false, Resolutions.SD, tileSet, skeleton);

    Point renderPoint = new Point(0, 0);

    layer.render(window.getGraphicsContext(), renderPoint.getX(), renderPoint.getY());


    double shiftAmount = 16;
    double mapWidth = tileSet.getTileWidth() * skeleton[0].length;
    double mapHeight = tileSet.getTileHeight() * skeleton.length;
    window.addKeyPressedHandler(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.LEFT && renderPoint.getX() > 0) {
        renderPoint.translate(-shiftAmount, 0);
      }
      if (keyEvent.getCode() == KeyCode.RIGHT && renderPoint.getX() + Resolutions.SD.getWidth() < mapWidth) {
        renderPoint.translate(shiftAmount, 0);
      }
      if (keyEvent.getCode() == KeyCode.UP && renderPoint.getY() > 0) {
        renderPoint.translate(0, -shiftAmount);
      }
      if (keyEvent.getCode() == KeyCode.DOWN && renderPoint.getY() + Resolutions.SD.getHeight() < mapHeight) {
        renderPoint.translate(0, shiftAmount);
      }
      window.clear();
      layer.render(window.getGraphicsContext(), renderPoint.getX(), renderPoint.getY());
    });

    primaryStage.setScene(window);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
