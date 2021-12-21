package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolutions;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.Request;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.types.AddForceRequest;
import org.storm.engine.request.types.StateChangeRequest;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.engine.state.State;
import org.storm.physics.entity.Entity;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.math.geometry.shapes.Circle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleTestState extends State {

  public ParticleTestState() {
    super(RequestQueue.UNLIMITED_REQUEST_SIZE);
  }

  @Override
  public void init() {
    this.entities.add(new ImmovableEntityImpl(0, 0, new AxisAlignedRectangle(0, 0, Resolutions.SD.getWidth(), 5)));
    this.entities.add(new ImmovableEntityImpl(0, 0, new AxisAlignedRectangle(0, 0, 5, Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableEntityImpl(Resolutions.SD.getWidth() - 5, 0, new AxisAlignedRectangle(Resolutions.SD.getWidth() - 5, 0, 5, Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableEntityImpl(0, Resolutions.SD.getHeight() - 5, new AxisAlignedRectangle(0, Resolutions.SD.getHeight() - 5, Resolutions.SD.getWidth(), 5)));

    List<Request> initialRequests = new ArrayList<>();
    Set<Point> usedPoints = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      Point topLeft = new Point(ThreadLocalRandom.current().nextInt(10, (int) (Resolutions.SD.getWidth()) - 10), ThreadLocalRandom.current().nextInt(10, (int) (Resolutions.SD.getHeight()) - 10));
      while (usedPoints.contains(topLeft)) {
        topLeft = new Point(ThreadLocalRandom.current().nextInt(10, (int) (Resolutions.SD.getWidth()) - 10), ThreadLocalRandom.current().nextInt(10, (int) (Resolutions.SD.getHeight()) - 10));
      }
      usedPoints.add(topLeft);

      Entity e = new EntityImpl(topLeft.getX(), topLeft.getY(), new Circle(topLeft.getX(), topLeft.getY(), 2), 0.5, 1);
      this.entities.add(e);
      initialRequests.add(new AddForceRequest(e, Direction.random().getVector().scale(2), 0.1));
    }

    this.requestQueue.submit(initialRequests);
  }

  @Override
  public void load() {

  }

  @Override
  public void unload() {

  }

  @Override
  public void process(ActionManager actionManager) {
    if (actionManager.isPerforming(KeyActionConstants.DOWN)) {
      this.requestQueue.submit(new StateChangeRequest(KeyActionConstants.DOWN));
    } else if (actionManager.isPerforming(KeyActionConstants.UP)) {
      this.requestQueue.submit(new StateChangeRequest(KeyActionConstants.UP));
    } else if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      this.requestQueue.submit(new TogglePhysicsRequest(false));
    }
  }

}
