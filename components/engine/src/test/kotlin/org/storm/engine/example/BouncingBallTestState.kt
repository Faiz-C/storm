package org.storm.engine.example

import org.storm.core.asset.AssetManager
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.core.input.action.ActionState
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.Circle

class BouncingBallTestState(assetManager: AssetManager) : SwitchableState(assetManager) {

    private val resolution = Context.RESOLUTION_IN_UNITS

    init {
        this.mutableEntities.add(
            ImmovableRectEntity(
                0.0,
                0.0,
                resolution.width.units,
                5.0.units
            )
        )
        this.mutableEntities.add(
            ImmovableRectEntity(
                0.0,
                0.0,
                5.0.units,
                resolution.height.units
            )
        )
        this.mutableEntities.add(
            ImmovableRectEntity(
                resolution.width - 5.units,
                0.0,
                5.0.units,
                resolution.height.units
            )
        )
        this.mutableEntities.add(
            ImmovableRectEntity(
                0.0,
                resolution.height - 5.units,
                resolution.width.units,
                5.0.units
            )
        )
        this.mutableEntities.add(
            EntityImpl(
                Circle(
                    25.0.units,
                    200.0.units,
                    15.0.units
                ),
                5.0.units,
                11.0,
                1.0
            ).also {
                it.addForce(Direction.SOUTH_EAST.vector.scale(100.0.units), 0.1.units)
            }
        )

        this.soundManager.loadSound("bgm")
        this.soundManager.adjustAllVolume(0.1)
    }

    override fun preload() {
    }

    override fun load() {
        soundManager.play("bgm")
    }

    override fun unload() {
        soundManager.pause("bgm")
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)
        if (actionState.isFirstActivation(KeyActionConstants.SPACE)) {
            Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
        }
    }

}
