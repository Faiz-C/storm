package org.storm.physics.visual;

import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.storm.core.ui.Resolutions;
import org.storm.core.ui.Window;
import org.storm.physics.Simulator;
import org.storm.physics.entity.Entity;
import org.storm.physics.entity.ImmovableEntity;
import org.storm.physics.entity.SimpleEntity;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.physics.transforms.UnitConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleTest extends Application {

  private final UnitConvertor unitConvertor = new UnitConvertor() {};

  private final Entity platformTop = new ImmovableEntity(
    new AxisAlignedRectangle(
      0,
      0,
      unitConvertor.toUnits(Resolutions.SD.getWidth()),
      unitConvertor.toUnits(5)
    )
  );

  private final Entity platformLeft = new ImmovableEntity(
    new AxisAlignedRectangle(
      0,
      0,
      unitConvertor.toUnits(5),
      unitConvertor.toUnits(Resolutions.SD.getHeight())
    )
  );

  private final Entity platformRight = new ImmovableEntity(
    new AxisAlignedRectangle(
      unitConvertor.toUnits(Resolutions.SD.getWidth() - 5),
      0,
      unitConvertor.toUnits(5),
      unitConvertor.toUnits(Resolutions.SD.getHeight())
    )
  );

  private final Entity platformBottom = new ImmovableEntity(
    new AxisAlignedRectangle(
      0,
      unitConvertor.toUnits(Resolutions.SD.getHeight() - 5),
      unitConvertor.toUnits(Resolutions.SD.getWidth()),
      unitConvertor.toUnits(5)
    )
  );

  private final List<Entity> entities = new ArrayList<>();

  private Simulator simulator;

  private final Color ballColour = Color.rgb(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255));

  @Override
  public void start(Stage stage) {
    // Make a Display
    Window window = new Window(Resolutions.SD);

    simulator = new Simulator(Resolutions.SD, 144, () -> render(window));

    this.entities.add(platformTop);
    this.entities.add(platformLeft);
    this.entities.add(platformRight);
    this.entities.add(platformBottom);

    for (int i = 0; i < 1000; i++) {
      Point topLeft = new Point(
        ThreadLocalRandom.current().nextInt(10, (int) unitConvertor.toUnits(Resolutions.SD.getWidth() - 10)),
        ThreadLocalRandom.current().nextInt(10, (int) unitConvertor.toUnits(Resolutions.SD.getHeight() - 10))
      );
      entities.add(new SimpleEntity(new Circle(topLeft.getX(), topLeft.getY(), unitConvertor.toUnits(2)), 3, 0.5, 1));
    }

    simulator.getPhysicsEngine().setEntities(entities);
    for (Entity e : this.entities) {
      e.addForce(Direction.random().getVector().scale(unitConvertor.toUnits(2)), 0.1);
    }


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

    GraphicsContext gc = window.getGraphicsContext();

    window.getGraphicsContext().save();
    this.simulator.getPhysicsEngine().render(gc, 0, 0);
    this.entities.forEach(e -> {
      if (e instanceof ImmovableEntity) {
        gc.setFill(Color.RED);
      } else {
        gc.setFill(ballColour);
      }
      e.transform().render(gc, 0, 0);
    });
    window.getGraphicsContext().restore();

  }

  public static void main(String[] args) {
    launch(args);
  }

}



