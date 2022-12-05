package org.storm.maps.layer;

import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.storm.core.ui.Resolution;
import org.storm.core.ui.Resolutions;
import org.storm.core.ui.Window;

public class BackgroundLayerTest extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Window window = new Window(Resolutions.HD);
    BackgroundLayer backgroundLayer = new BackgroundLayer("src/test/resources/background/testBackground.png", Resolutions.HD);

    backgroundLayer.render(window.getGraphicsContext(), 300, 200);

    primaryStage.setScene(window);
    primaryStage.show();
  }

}
