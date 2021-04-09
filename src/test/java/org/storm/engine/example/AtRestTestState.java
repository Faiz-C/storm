package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolutions;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.types.AddForceRequest;
import org.storm.engine.request.types.StateChangeRequest;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.engine.state.State;
import org.storm.physics.entity.Entity;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.sound.types.MediaSound;

import java.util.Arrays;

public class AtRestTestState extends State {

  private final Entity platform = new ImmovableEntityImpl(1, Resolutions.SD.getHeight() - 200, new AxisAlignedRectangle(1, Resolutions.SD.getHeight() - 20, Resolutions.SD.getWidth() - 2, 10));
  private final Entity repellingBall = new EntityImpl(75, 400, new AxisAlignedRectangle(75, 400, 20, 20), 10, 0.7);
  private final Entity repellingBall2 = new EntityImpl(150, 50, new AxisAlignedRectangle(150, 50, 20, 20), 10, 0.5);
  private final Entity repellingBall3 = new EntityImpl(225, 200, new AxisAlignedRectangle(225, 200, 20, 20), 10, 1);
  private final Entity repellingBall4 = new EntityImpl(300, 100, new AxisAlignedRectangle(300, 100, 20, 20), 10, 0.2);
  private final Vector GRAVITY = Direction.SOUTH.getVector().scale(25);

  @Override
  public void init() {
    this.entities.addAll(Arrays.asList(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4));
    this.soundManager.add("bgm", new MediaSound("src/test/resources/music/bgm.mp3", false));
    this.soundManager.adjustAllVolume(0.1);
    this.requestQueue.submit(
      new AddForceRequest(repellingBall, GRAVITY),
      new AddForceRequest(repellingBall, Direction.NORTH.getVector().scale(30), 2), // For fun
      new AddForceRequest(repellingBall2, GRAVITY),
      new AddForceRequest(repellingBall3, GRAVITY),
      new AddForceRequest(repellingBall4, GRAVITY)
    );
  }

  @Override
  public void unload() {
    this.soundManager.stop("bgm");
  }

  @Override
  public void load() {
    this.soundManager.play("bgm");
    this.requestQueue.submit(
      new AddForceRequest(repellingBall, GRAVITY),
      new AddForceRequest(repellingBall2, GRAVITY),
      new AddForceRequest(repellingBall3, GRAVITY),
      new AddForceRequest(repellingBall4, GRAVITY)
    );
  }

  @Override
  public void process(ActionManager actionManager) {
    if (actionManager.isPerforming(KeyActionConstants.UP)) {
      this.requestQueue.submit(new StateChangeRequest(KeyActionConstants.UP));
    } else if (actionManager.isPerforming(KeyActionConstants.LEFT)) {
      this.requestQueue.submit(new StateChangeRequest(KeyActionConstants.LEFT));
    } else if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
    this.requestQueue.submit(new TogglePhysicsRequest(false));
    }
  }

}
