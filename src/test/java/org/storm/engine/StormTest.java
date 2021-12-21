package org.storm.engine;

import javafx.application.Application;
import javafx.stage.Stage;
import org.storm.core.ui.Resolutions;
import org.storm.engine.example.AtRestTestState;
import org.storm.engine.example.ParticleTestState;
import org.storm.engine.example.BouncingBallTestState;
import org.storm.engine.example.TranslatorImpl;


public class StormTest extends Application {

  public void start(Stage primaryStage) {
    StormEngine stormEngine = new StormEngine(Resolutions.SD, 144, 240);

    stormEngine.addState(KeyActionConstants.DOWN, new AtRestTestState());
    stormEngine.addState(KeyActionConstants.UP, new BouncingBallTestState());
    stormEngine.addState(KeyActionConstants.LEFT, new ParticleTestState());
    stormEngine.addKeyRegister(new TranslatorImpl());

    stormEngine.setFpsChangeAllow(false);

    stormEngine.swapState(KeyActionConstants.LEFT);
    stormEngine.run();

    primaryStage.setScene(stormEngine.getWindow());
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
