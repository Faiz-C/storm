package org.storm.engine.example

import org.storm.core.input.ActionState
import org.storm.core.ui.Resolution
import org.storm.engine.KeyActionConstants
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.entity.Entity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.sound.types.MediaSound

class AtRestTestState : SwitchableState() {

    private val gravity = Direction.SOUTH.vector.scale(unitConvertor.toUnits(25.0))

    private val platform: Entity = ImmovableRectEntity(
        this.unitConvertor.toUnits(1.0),
        this.unitConvertor.toUnits(Resolution.SD.height - 20),
        this.unitConvertor.toUnits(Resolution.SD.width - 2),
        this.unitConvertor.toUnits(10.0)
    )

    private val repellingBall: Entity = EntityImpl(
        AABB(
            this.unitConvertor.toUnits(75.0),
            this.unitConvertor.toUnits(400.0),
            this.unitConvertor.toUnits(20.0),
            this.unitConvertor.toUnits(20.0)
        ),
        this.unitConvertor.toUnits(2.0),
        10.0,
        0.7
    ).also {
        it.addForce(gravity)
    }

    private val repellingBall2: Entity = EntityImpl(
        Circle(
            this.unitConvertor.toUnits(150.0),
            this.unitConvertor.toUnits(50.0),
            this.unitConvertor.toUnits(20.0)
        ),
        this.unitConvertor.toUnits(2.0),
        10.0,
        0.5
    ).also {
        it.addForce(gravity)
    }

    private val repellingBall3: Entity = EntityImpl(
        AABB(
            this.unitConvertor.toUnits(225.0),
            this.unitConvertor.toUnits(200.0),
            this.unitConvertor.toUnits(20.0),
            this.unitConvertor.toUnits(20.0)
        ),
        this.unitConvertor.toUnits(2.0),
        10.0,
        1.0
    ).also {
        it.addForce(gravity)
    }

    private val repellingBall4: Entity = EntityImpl(
        AABB(
            this.unitConvertor.toUnits(300.0),
            this.unitConvertor.toUnits(100.0),
            this.unitConvertor.toUnits(20.0),
            this.unitConvertor.toUnits(20.0)
        ),
        this.unitConvertor.toUnits(2.0),
        10.0,
        0.2
    ).also {
        it.addForce(gravity)
    }

    override fun preload(requestQueue: RequestQueue) {
        this.mutableEntities.addAll(listOf(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4))
        this.soundManager.add("bgm", MediaSound("music/bgm.mp3", resource = true))
        this.soundManager.adjustAllVolume(0.1)
    }

    override fun unload(requestQueue: RequestQueue) {
        soundManager.stop("bgm")
    }

    override fun load(requestQueue: RequestQueue) {
        soundManager.play("bgm")
    }

    override suspend fun process(actionState: ActionState, requestQueue: RequestQueue) {
        super.process(actionState, requestQueue)
        if (actionState.isFirstTrigger(KeyActionConstants.SPACE)) {
            requestQueue.submit(TogglePhysicsRequest(false))
        }
    }

}
