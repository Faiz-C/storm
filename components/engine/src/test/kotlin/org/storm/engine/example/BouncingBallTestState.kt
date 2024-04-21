package org.storm.engine.example

import org.storm.core.asset.AssetManager
import org.storm.core.context.Context
import org.storm.core.input.ActionState
import org.storm.core.ui.Resolution
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.context.UNIT_CONVERTOR
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.Circle

class BouncingBallTestState(assetManager: AssetManager) : SwitchableState(assetManager) {
    init {
        this.mutableEntities.add(
            ImmovableRectEntity(
                0.0,
                0.0,
                Context.UNIT_CONVERTOR.toUnits(Resolution.SD.width),
                Context.UNIT_CONVERTOR.toUnits(5.0)
            )
        )
        this.mutableEntities.add(
            ImmovableRectEntity(
                0.0,
                0.0,
                Context.UNIT_CONVERTOR.toUnits(5.0),
                Context.UNIT_CONVERTOR.toUnits(Resolution.SD.height)
            )
        )
        this.mutableEntities.add(
            ImmovableRectEntity(
                Context.UNIT_CONVERTOR.toUnits(Resolution.SD.width - 5),
                0.0,
                Context.UNIT_CONVERTOR.toUnits(5.0),
                Context.UNIT_CONVERTOR.toUnits(Resolution.SD.height)
            )
        )
        this.mutableEntities.add(
            ImmovableRectEntity(
                0.0,
                Context.UNIT_CONVERTOR.toUnits(Resolution.SD.height - 5),
                Context.UNIT_CONVERTOR.toUnits(Resolution.SD.width),
                Context.UNIT_CONVERTOR.toUnits(5.0)
            )
        )
        this.mutableEntities.add(
            EntityImpl(
                Circle(
                    Context.UNIT_CONVERTOR.toUnits(25.0),
                    Context.UNIT_CONVERTOR.toUnits(200.0),
                    Context.UNIT_CONVERTOR.toUnits(15.0)
                ),
                Context.UNIT_CONVERTOR.toUnits(5.0),
                11.0,
                1.0
            ).also {
                it.addForce(Direction.SOUTH_EAST.vector.scale(Context.UNIT_CONVERTOR.toUnits(100.0)), 0.1)
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
        if (actionState.isFirstTrigger(KeyActionConstants.SPACE)) {
            Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
        }
    }

}
