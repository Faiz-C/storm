package org.storm.engine.example

import org.storm.core.input.action.ActionManager
import org.storm.core.ui.Resolution
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.sound.types.MediaSound

class BouncingBallTestState : SwitchableState() {
  init {
    this.mutableEntities.add(
      ImmovableRectEntity(
        0.0,
        0.0,
        this.unitConvertor.toUnits(Resolution.SD.width),
        this.unitConvertor.toUnits(5.0)
      )
    )
    this.mutableEntities.add(
      ImmovableRectEntity(
        0.0,
        0.0,
        this.unitConvertor.toUnits(5.0),
        this.unitConvertor.toUnits(Resolution.SD.height)
      )
    )
    this.mutableEntities.add(
      ImmovableRectEntity(
        this.unitConvertor.toUnits(Resolution.SD.width - 5),
        0.0,
        this.unitConvertor.toUnits(5.0),
        this.unitConvertor.toUnits(Resolution.SD.height)
      )
    )
    this.mutableEntities.add(
      ImmovableRectEntity(
        0.0,
        this.unitConvertor.toUnits(Resolution.SD.height - 5),
        this.unitConvertor.toUnits(Resolution.SD.width),
        this.unitConvertor.toUnits(5.0)
      )
    )
    this.mutableEntities.add(
      EntityImpl(
        Circle(
          this.unitConvertor.toUnits(25.0),
          this.unitConvertor.toUnits(200.0),
          this.unitConvertor.toUnits(15.0)
        ),
        this.unitConvertor.toUnits(5.0),
        11.0,
        1.0
      ).also {
        it.addForce(Direction.SOUTH_EAST.vector.scale(this.unitConvertor.toUnits(100.0)), 0.1)
      }
    )

    this.soundManager.add("bgm", MediaSound("music/bgm.mp3", resource = true))
    this.soundManager.adjustAllVolume(0.1)
  }

  override fun preload(requestQueue: RequestQueue) {
  }

  override fun load(requestQueue: RequestQueue) {
    soundManager.play("bgm")
  }

  override fun unload(requestQueue: RequestQueue) {
    soundManager.pause("bgm")
  }

  override fun process(actionManager: ActionManager, requestQueue: RequestQueue) {
    super.process(actionManager, requestQueue)
    if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      requestQueue.submit(TogglePhysicsRequest(false))
    }
  }

}
