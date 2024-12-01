package org.storm.engine.example

import org.storm.core.asset.AssetManager
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.core.input.ActionState
import org.storm.core.render.geometry.Point
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.entity.Entity
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import java.util.concurrent.ThreadLocalRandom

class ParticleTestState(assetManager: AssetManager) : SwitchableState(assetManager) {

    private val resolution = Context.RESOLUTION_IN_UNITS

    init {
        val boundingBox = ImmovableEntity(
            mutableMapOf(
                "platformTop" to AABB(
                    0.0,
                    0.0,
                    resolution.width,
                    5.0.units
                ),
                "platformRight" to AABB(
                    0.0,
                    0.0,
                    5.0.units,
                    resolution.height.units
                ),
                "platformBottom" to AABB(
                    resolution.width - 5.units,
                    0.0,
                    5.0.units,
                    resolution.height.units
                ),
                "platformLeft" to AABB(
                    0.0,
                    resolution.width - 5.units,
                    resolution.height.units,
                    5.0.units
                )
            )
        )
        this.mutableEntities.add(boundingBox)
    }

    override fun preload() {
        val usedPoints: MutableSet<Point> = mutableSetOf()

        for (i in 0..400) {
            var topLeft = Point(
                ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.width - 5.0).toInt()).units,
                ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.height - 5).toInt()).units
            )

            while (usedPoints.contains(topLeft)) {
                topLeft = Point(
                    ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.width  - 5).toInt()).units,
                    ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.height - 5).toInt()).units
                )
            }

            usedPoints.add(topLeft)

            val e: Entity = EntityImpl(
                Circle(topLeft.x, topLeft.y, 2.0.units),
                2.0.units,
                0.5,
                1.0
            ).also {
                it.addForce(Direction.random().vector.scale(2.0), 0.1.units)
            }

            mutableEntities.add(e)
        }
    }

    override fun load() {
        Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)

        if (actionState.isFirstTrigger(KeyActionConstants.SPACE)) {
            Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
        }
    }

}
