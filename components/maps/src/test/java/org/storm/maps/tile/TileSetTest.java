package org.storm.maps.tile;

import javafx.application.Application;
import javafx.stage.Stage;
import org.storm.core.ui.Resolutions;
import org.storm.core.ui.Window;

public class TileSetTest extends Application{

  @Override
  public void start(Stage primaryStage) throws Exception {
    Window window = new Window(Resolutions.SD);
    TileSet tileSet = new TileSet("src/test/resources/tiles/testTileSet.png", 32, 32);

    window.getGraphicsContext().drawImage(tileSet.getTileImage(0), 100, 50);
    window.getGraphicsContext().drawImage(tileSet.getTileImage(1), 150, 100);
    window.getGraphicsContext().drawImage(tileSet.getTileImage(2), 200, 150);

    primaryStage.setScene(window);
    primaryStage.show();
  }

}
