package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.input.ActionState
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.constants.Vectors
import org.storm.physics.context.UNIT_CONVERTOR
import org.storm.physics.entity.Entity
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.sound.types.JfxSound

class CircleCornerTestState : SwitchableState() {

    private val player: Entity = EntityImpl(
        Circle(
            Context.UNIT_CONVERTOR.toUnits(25.0),
            Context.UNIT_CONVERTOR.toUnits(200.0),
            Context.UNIT_CONVERTOR.toUnits(10.0)
        ),
        Context.UNIT_CONVERTOR.toUnits(2.0),
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
                Context.UNIT_CONVERTOR.toUnits(200.0),
                Context.UNIT_CONVERTOR.toUnits(200.0),
                Context.UNIT_CONVERTOR.toUnits(100.0),
                Context.UNIT_CONVERTOR.toUnits(100.0)
            )
        )
        this.mutableEntities.add(player)

        soundManager.add("bgm", JfxSound("music/bgm.mp3", resource = true))
        soundManager.adjustAllVolume(0.1)
    }

    override fun load() {
        soundManager.play("bgm")
        Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
    }

    override fun unload() {
        soundManager.pause("bgm")
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)

        this.player.velocity = Vectors.ZERO_VECTOR
        this.movementVectors.map { (action, vector) ->
            if (actionState.isActionHeld(action, 1)) {
                this.player.velocity += vector.scale(this.player.speed)
            }
        }
    }

}
