package org.storm.engine.example;

import org.storm.core.input.action.ActionManager;
import org.storm.engine.KeyActionConstants;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.types.TogglePhysicsRequest;
import org.storm.engine.request.types.VelocityUpdateRequest;
import org.storm.physics.constants.Vectors;
import org.storm.physics.entity.Entity;
import org.storm.physics.math.Vector;
import org.storm.physics.math.geometry.shapes.Circle;
import org.storm.sound.types.MediaSound;

public class CircleCornerTestState extends SwitchableState {

  private final Entity player = new EntityImpl(
    new Circle(
      this.unitConvertor.toUnits(25),
      this.unitConvertor.toUnits(200),
      this.unitConvertor.toUnits(10)
    ),
    this.unitConvertor.toUnits(2),
    10,
    0.1
  );

  public CircleCornerTestState() {
    super();
    this.entities.add(new ImmovableRectEntity(this.unitConvertor.toUnits(200), this.unitConvertor.toUnits(200), this.unitConvertor.toUnits(100), this.unitConvertor.toUnits(100)));
    this.entities.add(player);
    this.soundManager.add("bgm", new MediaSound("src/test/resources/music/bgm.mp3", true));
    this.soundManager.adjustAllVolume(0.1);
  }

  @Override
  public void load(RequestQueue requestQueue) {
    this.soundManager.play("bgm");
    requestQueue.submit(new TogglePhysicsRequest(false));
  }

  @Override
  public void unload(RequestQueue requestQueue) {
    this.soundManager.pause("bgm");
  }

  @Override
  public void process(ActionManager actionManager, RequestQueue requestQueue) {
    super.process(actionManager, requestQueue);

    Vector velocity = Vectors.ZERO_VECTOR;

    if (actionManager.isPerforming(KeyActionConstants.W)) {
      velocity = velocity.add(Vectors.UNIT_NORTH.scale(this.player.getSpeed()));
    }
    else if (actionManager.isPerforming(KeyActionConstants.S)) {
      velocity = velocity.add(Vectors.UNIT_SOUTH.scale(this.player.getSpeed()));
    }
    else if (actionManager.isPerforming(KeyActionConstants.D)) {
      velocity = velocity.add(Vectors.UNIT_EAST.scale(this.player.getSpeed()));
    }
    else if (actionManager.isPerforming(KeyActionConstants.A)) {
      velocity = velocity.add(Vectors.UNIT_WEST.scale(this.player.getSpeed()));
    }

    requestQueue.submit(new VelocityUpdateRequest(player, velocity));
  }
}
