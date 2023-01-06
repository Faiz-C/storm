package org.storm.engine.example

import org.storm.core.input.action.ActionManager
import org.storm.core.ui.Resolution
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.entity.Entity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.Circle
import java.util.concurrent.ThreadLocalRandom

class ParticleTestState : SwitchableState() {

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
  }

  override fun preload(requestQueue: RequestQueue) {
    val usedPoints: MutableSet<Point> = mutableSetOf()

    for (i in 0..999) {
      var topLeft = Point(
        ThreadLocalRandom.current().nextInt(5, this.unitConvertor.toUnits(Resolution.SD.width - 5).toInt()).toDouble(),
        ThreadLocalRandom.current().nextInt(5, this.unitConvertor.toUnits(Resolution.SD.height - 5).toInt()).toDouble()
      )

      while (usedPoints.contains(topLeft)) {
        topLeft = Point(
          ThreadLocalRandom.current().nextInt(5, this.unitConvertor.toUnits(Resolution.SD.width - 5).toInt()).toDouble(),
          ThreadLocalRandom.current().nextInt(5, this.unitConvertor.toUnits(Resolution.SD.height - 5).toInt()).toDouble()
        )
      }

      usedPoints.add(topLeft)

      val e: Entity = EntityImpl(
        Circle(topLeft.x, topLeft.y, this.unitConvertor.toUnits(2.0)),
        this.unitConvertor.toUnits(2.0),
        0.5,
        1.0
      ).also {
        it.addForce(Direction.random().vector.scale(this.unitConvertor.toUnits(2.0)), 0.1)
      }

      mutableEntities.add(e)
    }
  }

  override fun load(requestQueue: RequestQueue) {
    requestQueue.submit(TogglePhysicsRequest(true))
  }

  override fun process(actionManager: ActionManager, requestQueue: RequestQueue) {
    super.process(actionManager, requestQueue)

    if (actionManager.isPerforming(KeyActionConstants.SPACE)) {
      requestQueue.submit(TogglePhysicsRequest(false))
    }
  }

}
