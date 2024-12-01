package org.storm.engine.example

import org.storm.core.asset.AssetManager
import org.storm.core.context.Context
import org.storm.core.extensions.units
import org.storm.core.input.ActionState
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.entity.Entity
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.Circle

class CircleCornerTestState(assetManager: AssetManager) : SwitchableState(assetManager) {

    private val player: Entity = EntityImpl(
        Circle(
            25.0.units,
            200.0.units,
            10.0.units
        ),
        2.0.units,
        10.0,
        0.1
    )

    private val movementVectors = mapOf(
        KeyActionConstants.W to Vector.UNIT_NORTH,
        KeyActionConstants.S to Vector.UNIT_SOUTH,
        KeyActionConstants.A to Vector.UNIT_WEST,
        KeyActionConstants.D to Vector.UNIT_EAST,
    )

    init {
        this.mutableEntities.add(
            ImmovableRectEntity(
                200.0.units,
                200.0.units,
                100.0.units,
                100.0.units
            )
        )
        this.mutableEntities.add(player)

        this.soundManager.loadSound("bgm")
        this.soundManager.adjustAllVolume(0.1)
    }

    override fun load() {
        this.soundManager.play("bgm")
        Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
    }

    override fun unload() {
        this.soundManager.pause("bgm")
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)

        this.player.velocity = Vector.ZERO_VECTOR
        this.movementVectors.map { (action, vector) ->
            if (actionState.isActionHeld(action, 1)) {
                this.player.velocity += vector.scale(this.player.speed)
            }
        }
    }

}
