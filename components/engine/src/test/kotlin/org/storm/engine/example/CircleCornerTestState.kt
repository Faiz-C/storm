package org.storm.engine.example

import org.storm.core.input.action.ActionManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.constants.Vectors
import org.storm.physics.entity.Entity
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.sound.types.MediaSound

class CircleCornerTestState : SwitchableState() {

  private val player: Entity = EntityImpl(
    Circle(
      this.unitConvertor.toUnits(25.0),
      this.unitConvertor.toUnits(200.0),
      this.unitConvertor.toUnits(10.0)
    ),
    this.unitConvertor.toUnits(2.0),
    10.0,
    0.1
  )

  init {
    this.mutableEntities.add(
      ImmovableRectEntity(
        this.unitConvertor.toUnits(200.0),
        this.unitConvertor.toUnits(200.0),
        this.unitConvertor.toUnits(100.0),
        this.unitConvertor.toUnits(100.0)
      )
    )
    this.mutableEntities.add(player)

    soundManager.add("bgm", MediaSound("music/bgm.mp3", resource = true))
    soundManager.adjustAllVolume(0.1)
  }

  override fun load(requestQueue: RequestQueue) {
    soundManager.play("bgm")
    requestQueue.submit(TogglePhysicsRequest(false))
  }

  override fun unload(requestQueue: RequestQueue) {
    soundManager.pause("bgm")
  }

  override fun process(actionManager: ActionManager, requestQueue: RequestQueue) {
    super.process(actionManager, requestQueue)

    this.player.velocity = if (actionManager.isActive(KeyActionConstants.W)) {
      Vectors.UNIT_NORTH.scale(this.player.speed)
    } else if (actionManager.isActive(KeyActionConstants.S)) {
      Vectors.UNIT_SOUTH.scale(this.player.speed)
    } else if (actionManager.isActive(KeyActionConstants.D)) {
      Vectors.UNIT_EAST.scale(this.player.speed)
    } else if (actionManager.isActive(KeyActionConstants.A)) {
      Vectors.UNIT_WEST.scale(this.player.speed)
    } else {
      Vectors.ZERO_VECTOR
    }
  }

}
