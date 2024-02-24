package org.storm.engine.example

import org.storm.core.input.ActionState
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

    private val movementVectors = mapOf(
        KeyActionConstants.W to Vectors.UNIT_NORTH,
        KeyActionConstants.S to Vectors.UNIT_SOUTH,
        KeyActionConstants.A to Vectors.UNIT_WEST,
        KeyActionConstants.D to Vectors.UNIT_EAST,
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

    override fun process(actionState: ActionState, requestQueue: RequestQueue) {
        super.process(actionState, requestQueue)

        this.player.velocity = Vectors.ZERO_VECTOR
        this.movementVectors.map { (action, vector) ->
            if (actionState.isActionHeld(action, 1)) {
                this.player.velocity += vector.scale(this.player.speed)
            }
        }
    }

}
