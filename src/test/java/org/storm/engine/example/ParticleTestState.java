package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolutions;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.Request;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.types.AddForceRequest;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.physics.entity.Entity;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.geometry.Point;
import org.storm.physics.math.geometry.shapes.Circle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleTestState extends SwitchableState {

  public ParticleTestState() {
    super();
    this.entities.add(new ImmovableRectEntity(0, 0, this.unitConvertor.toUnits(Resolutions.SD.getWidth()), this.unitConvertor.toUnits(5)));
    this.entities.add(new ImmovableRectEntity(0, 0, this.unitConvertor.toUnits(5), this.unitConvertor.toUnits(Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableRectEntity(this.unitConvertor.toUnits(Resolutions.SD.getWidth() - 5), 0, this.unitConvertor.toUnits(5), this.unitConvertor.toUnits(Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableRectEntity(0, this.unitConvertor.toUnits(Resolutions.SD.getHeight() - 5), this.unitConvertor.toUnits(Resolutions.SD.getWidth()), this.unitConvertor.toUnits(5)));
  }

  @Override
  public void preload(RequestQueue requestQueue) {
    List<Request> initialRequests = new ArrayList<>();
    Set<Point> usedPoints = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      Point topLeft = new Point(ThreadLocalRandom.current().nextInt(10, (int) this.unitConvertor.toUnits(Resolutions.SD.getWidth() - 10)), ThreadLocalRandom.current().nextInt(10, (int) this.unitConvertor.toUnits(Resolutions.SD.getHeight() - 10)));
      while (usedPoints.contains(topLeft)) {
        topLeft = new Point(ThreadLocalRandom.current().nextInt(10, (int) this.unitConvertor.toUnits(Resolutions.SD.getWidth() - 10)), ThreadLocalRandom.current().nextInt(10, (int) this.unitConvertor.toUnits(Resolutions.SD.getHeight() - 10)));
      }
      usedPoints.add(topLeft);

      Entity e = new EntityImpl(new Circle(topLeft.getX(), topLeft.getY(), this.unitConvertor.toUnits(2)), this.unitConvertor.toUnits(2), 0.5, 1);
      this.entities.add(e);
      initialRequests.add(new AddForceRequest(e, Direction.random().getVector().scale(this.unitConvertor.toUnits(2)), 0.1));
    }

    requestQueue.submit(initialRequests);
  }

  @Override
  public void process(ActionManager actionManager, RequestQueue requestQueue) {
    super.process(actionManager, requestQueue);
    if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      requestQueue.submit(new TogglePhysicsRequest(false));
    }
  }

}
