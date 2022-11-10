package org.storm.physics.visual;

import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.storm.core.ui.Resolutions;
import org.storm.core.ui.Window;
import org.storm.physics.entity.Entity;
import org.storm.physics.entity.ImmovableEntity;
import org.storm.physics.entity.SimpleEntity;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.transforms.UnitConvertor;

import java.util.Arrays;

public class VisualAtRestTest extends Application {

  private final UnitConvertor unitConvertor = new UnitConvertor() {};

  private final Entity platform = new ImmovableEntity(
    new AxisAlignedRectangle(
      unitConvertor.toUnits(1),
      unitConvertor.toUnits(Resolutions.SD.getHeight() - 20),
      unitConvertor.toUnits(Resolutions.SD.getWidth() - 2),
      unitConvertor.toUnits(10)
    ));

  private final Entity repellingBall = new SimpleEntity(
    new AxisAlignedRectangle(
      unitConvertor.toUnits(75),
      unitConvertor.toUnits(400),
      unitConvertor.toUnits(20),
      unitConvertor.toUnits(20)
    ),
    3,
    10,
    0.7
  );

  private final Entity repellingBall2 = new SimpleEntity(
    new AxisAlignedRectangle(
      unitConvertor.toUnits(150),
      unitConvertor.toUnits(50),
      unitConvertor.toUnits(20),
      unitConvertor.toUnits(20)
    ),
    3,
    10,
    0.5
  );

  private final Entity repellingBall3 = new SimpleEntity(
    new AxisAlignedRectangle(
      unitConvertor.toUnits(220),
      unitConvertor.toUnits(200),
      unitConvertor.toUnits(20),
      unitConvertor.toUnits(20)
    ),
    3,
    10,
    1
  );

  private final Entity repellingBall4 = new SimpleEntity(
    new AxisAlignedRectangle(
      unitConvertor.toUnits(300),
      unitConvertor.toUnits(100),
      unitConvertor.toUnits(20),
      unitConvertor.toUnits(20)
    ),
    3,
    10,
    0.2
  );

  private Simulator simulator;

  @Override
  public void start(Stage stage) {
    // Make a Display
    Window window = new Window(Resolutions.SD);

    simulator = new Simulator(Resolutions.SD, 400, () -> render(window));
    simulator.getPhysicsEngine().setEntities(Arrays.asList(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4));
    repellingBall.addForce(Direction.SOUTH.getVector().scale(unitConvertor.toUnits(10)));
    repellingBall.addForce(Direction.NORTH.getVector().scale(unitConvertor.toUnits(30)), 2);
    repellingBall2.addForce(Direction.SOUTH.getVector().scale(unitConvertor.toUnits(30)));
    repellingBall3.addForce(Direction.SOUTH.getVector().scale(unitConvertor.toUnits(25)));
    repellingBall4.addForce(Direction.SOUTH.getVector().scale(unitConvertor.toUnits(25)));
    simulator.setPaused(true);

    window.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.SPACE) {
        simulator.setPaused(!simulator.isPaused());
      }
    });

    simulator.simulate();

    stage.setScene(window);
    stage.show();
  }

  private void render(Window window) {
    window.clear();

    simulator.getPhysicsEngine().render(window.getGraphicsContext(), 0, 0);

    platform.transform().render(window.getGraphicsContext(), 0, 0);
    repellingBall.transform().render(window.getGraphicsContext(), 0, 0);
    repellingBall2.transform().render(window.getGraphicsContext(), 0, 0);
    repellingBall3.transform().render(window.getGraphicsContext(), 0, 0);
    repellingBall4.transform().render(window.getGraphicsContext(), 0, 0);
  }

  public static void main(String[] args) {
    launch(args);
  }

}

