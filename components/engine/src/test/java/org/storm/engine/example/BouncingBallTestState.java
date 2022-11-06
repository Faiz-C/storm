package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolutions;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.types.AddForceRequest;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.physics.enums.Direction;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.sound.types.MediaSound;

public class BouncingBallTestState extends SwitchableState {

  public BouncingBallTestState() {
    super();
    this.entities.add(new ImmovableRectEntity(0, 0, this.unitConvertor.toUnits(Resolutions.SD.getWidth()), this.unitConvertor.toUnits(5)));
    this.entities.add(new ImmovableRectEntity(0, 0, this.unitConvertor.toUnits(5), this.unitConvertor.toUnits(Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableRectEntity(unitConvertor.toUnits(Resolutions.SD.getWidth() - 5), 0, this.unitConvertor.toUnits(5), this.unitConvertor.toUnits(Resolutions.SD.getHeight())));
    this.entities.add(new ImmovableRectEntity(0, this.unitConvertor.toUnits(Resolutions.SD.getHeight() - 5), this.unitConvertor.toUnits(Resolutions.SD.getWidth()), this.unitConvertor.toUnits(5)));
    this.entities.add(new EntityImpl(new Circle(this.unitConvertor.toUnits(25), this.unitConvertor.toUnits(200), this.unitConvertor.toUnits(15)), this.unitConvertor.toUnits(5), 11, 1));
    this.soundManager.add("bgm", new MediaSound("src/test/resources/music/bgm.mp3", true));
    this.soundManager.adjustAllVolume(0.1);
  }

  @Override
  public void preload(RequestQueue requestQueue) {
    requestQueue.submit(new AddForceRequest(this.entities.get(4), Direction.SOUTH_EAST.getVector().scale(this.unitConvertor.toUnits(100)), 0.1));
  }

  @Override
  public void load(RequestQueue requestQueue) {
    this.soundManager.play("bgm");
  }

  @Override
  public void unload(RequestQueue requestQueue) {
    this.soundManager.pause("bgm");
  }

  @Override
  public void process(ActionManager actionManager, RequestQueue requestQueue) {
    super.process(actionManager, requestQueue);
    if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      requestQueue.submit(new TogglePhysicsRequest(false));
    }
  }
}
