package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolutions;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.types.AddForceRequest;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.physics.entity.Entity;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.shapes.AABB;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.sound.types.MediaSound;

import java.util.Arrays;

public class AtRestTestState extends SwitchableState {

  private final Entity platform = new ImmovableRectEntity(
    this.unitConvertor.toUnits(1),
    this.unitConvertor.toUnits(Resolutions.SD.getHeight() - 20),
    this.unitConvertor.toUnits(Resolutions.SD.getWidth() - 2),
    this.unitConvertor.toUnits(10)
  );

  private final Entity repellingBall = new EntityImpl(
    new AABB(
      this.unitConvertor.toUnits(75),
      this.unitConvertor.toUnits(400),
      this.unitConvertor.toUnits(20),
      this.unitConvertor.toUnits(20)
    ),
    this.unitConvertor.toUnits(2),
    10,
    0.7
  );

  private final Entity repellingBall2 = new EntityImpl(
    new Circle(
      this.unitConvertor.toUnits(150),
      this.unitConvertor.toUnits(50),
      this.unitConvertor.toUnits(20)
    ),
    this.unitConvertor.toUnits(2),
    10,
    0.5
  );

  private final Entity repellingBall3 = new EntityImpl(
    new AABB(
      unitConvertor.toUnits(225),
      unitConvertor.toUnits(200),
      unitConvertor.toUnits(20),
      unitConvertor.toUnits(20)
    ),
    this.unitConvertor.toUnits(2),
    10,
    1
  );

  private final Entity repellingBall4 = new EntityImpl(
    new AABB(
      unitConvertor.toUnits(300),
      unitConvertor.toUnits(100),
      unitConvertor.toUnits(20),
      unitConvertor.toUnits(20)
    ),
    this.unitConvertor.toUnits(2),
    10,
    0.2
  );

  private final Vector GRAVITY = Direction.SOUTH.getVector().scale(unitConvertor.toUnits(25));

  public AtRestTestState() {
    super();
    this.entities.addAll(Arrays.asList(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4));
    this.soundManager.add("bgm", new MediaSound("src/test/resources/music/bgm.mp3", false));
    this.soundManager.adjustAllVolume(0.1);
  }

  @Override
  public void preload(RequestQueue requestQueue) {
    requestQueue.submit(
      new AddForceRequest(repellingBall, GRAVITY),
      new AddForceRequest(repellingBall, GRAVITY),
      new AddForceRequest(repellingBall2, GRAVITY),
      new AddForceRequest(repellingBall3, GRAVITY),
      new AddForceRequest(repellingBall4, GRAVITY)
    );
  }

  @Override
  public void unload(RequestQueue requestQueue) {
    this.soundManager.stop("bgm");
  }

  @Override
  public void load(RequestQueue requestQueue) {
    this.soundManager.play("bgm");
    requestQueue.submit(
      new AddForceRequest(repellingBall, GRAVITY),
      new AddForceRequest(repellingBall2, GRAVITY),
      new AddForceRequest(repellingBall3, GRAVITY),
      new AddForceRequest(repellingBall4, GRAVITY)
    );
  }

  @Override
  public void process(ActionManager actionManager, RequestQueue requestQueue) {
    super.process(actionManager, requestQueue);
    if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      requestQueue.submit(new TogglePhysicsRequest(false));
    }
  }
}
