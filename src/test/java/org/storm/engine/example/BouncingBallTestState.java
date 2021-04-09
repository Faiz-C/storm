package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolutions;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.types.AddForceRequest;
import org.storm.engine.request.types.StateChangeRequest;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.engine.state.State;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.sound.types.MediaSound;

public class BouncingBallTestState extends State {

  @Override
  public void init() {
    this.entities.add(new ImmovableEntityImpl(0, 0, new AxisAlignedRectangle(0, 0, Resolutions.SD.getWidth(), 5)));
    this.entities.add(new ImmovableEntityImpl(0, 0, new AxisAlignedRectangle(0, 0, 5, Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableEntityImpl(Resolutions.SD.getWidth() - 5, 0, new AxisAlignedRectangle(Resolutions.SD.getWidth() - 5, 0, 5, Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableEntityImpl(0, Resolutions.SD.getHeight() - 5, new AxisAlignedRectangle(0, Resolutions.SD.getHeight() - 5, Resolutions.SD.getWidth(), 5)));
    this.entities.add(new EntityImpl(25, 200, new Circle(25, 200, 5), 11, 1));
    this.soundManager.add("bgm", new MediaSound("src/test/resources/music/bgm.mp3", true));
    this.soundManager.adjustAllVolume(0.1);

    this.requestQueue.submit(new AddForceRequest(this.entities.get(4), Direction.SOUTH_EAST.getVector().scale(100), 0.1));
  }

  @Override
  public void load() {
    this.soundManager.play("bgm");
  }

  @Override
  public void unload() {
    this.soundManager.pause("bgm");
  }

  @Override
  public void process(ActionManager actionManager) {
    if (actionManager.isPerforming(KeyActionConstants.DOWN)) {
      this.requestQueue.submit(new StateChangeRequest(KeyActionConstants.DOWN));
    } else if (actionManager.isPerforming(KeyActionConstants.LEFT)) {
      this.requestQueue.submit(new StateChangeRequest(KeyActionConstants.LEFT));
    } else if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      this.requestQueue.submit(new TogglePhysicsRequest(false));
    }
  }
}
