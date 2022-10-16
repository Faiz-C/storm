package org.storm.engine;

import javafx.application.Application;
import javafx.stage.Stage;
import org.storm.core.input.action.SimpleActionManager;
import org.storm.core.ui.Resolution;
import org.storm.core.ui.Resolutions;
import org.storm.engine.example.*;
import org.storm.engine.request.RequestQueue;
import org.storm.physics.ImpulseResolutionPhysicsEngine;
import org.storm.physics.transforms.UnitConvertor;

public class StormTest extends Application {

  public void start(Stage primaryStage) {
    Resolution resolution = Resolutions.SD;
    StormEngine stormEngine = new StormEngine(
      new ImpulseResolutionPhysicsEngine(resolution),
      new SimpleActionManager(),
      new UnitConvertor() {},
      resolution,
      RequestQueue.UNLIMITED_REQUEST_SIZE,
      144,
      240
    );

    stormEngine.addState(KeyActionConstants.ONE, new AtRestTestState());
    stormEngine.addState(KeyActionConstants.TWO, new BouncingBallTestState());
    stormEngine.addState(KeyActionConstants.THREE, new ParticleTestState());
    stormEngine.addState(KeyActionConstants.FOUR, new CircleCornerTestState());
    stormEngine.addKeyRegister(new TranslatorImpl());

    stormEngine.setFpsChangeAllow(false);

    stormEngine.swapState(KeyActionConstants.ONE);
    stormEngine.run();

    primaryStage.setScene(stormEngine.getWindow());
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
