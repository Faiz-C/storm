package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.core.input.action.ActionState
import org.storm.core.graphics.geometry.Point
import org.storm.core.sound.SoundManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.PhysicsEngine
import org.storm.physics.collision.Collider
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import java.util.concurrent.ThreadLocalRandom

class ParticleTestState : SwitchableState() {

    private val resolution = Context.RESOLUTION_IN_UNITS

    override val colliders: Set<Collider> = run {
        val colliders = mutableSetOf(
            Collider(
                mutableMapOf(
                    "platformTop" to AABB(
                        0.0,
                        0.0,
                        resolution.width,
                        5.0.units
                    ),
                    "platformLeft" to AABB(
                        0.0,
                        0.0,
                        5.0.units,
                        resolution.height
                    ),
                    "platformRight" to AABB(
                        resolution.width - 5.units,
                        0.0,
                        5.0.units,
                        resolution.height
                    ),
                    "platformBottom" to AABB(
                        0.0,
                        resolution.height - 5.units,
                        resolution.width,
                        5.0.units
                    )
                ),
                Double.POSITIVE_INFINITY,
                1.0
            )
        )

        val usedPoints: MutableSet<Point> = mutableSetOf()

        for (i in 0..400) {
            var topLeft = Point(
                ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.width - 5).toInt()).units,
                ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.height - 5).toInt()).units
            )

            while (usedPoints.contains(topLeft)) {
                topLeft = Point(
                    ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.width - 5).toInt()).units,
                    ThreadLocalRandom.current().nextInt(5, (Context.RESOLUTION.height - 5).toInt()).units
                )
            }

            usedPoints.add(topLeft)

            val c = Collider(
                Circle(topLeft.x, topLeft.y, 2.0.units),
                5.0.units,
                1.0
            )

            colliders.add(c)
        }

        colliders
    }

    override suspend fun onRegister(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        colliders.forEach {
            physicsEngine.applyForce(Direction.random().vector.scale(2.0.units), it, 0.1)
        }
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)

        if (actionState.isFirstActivation(KeyActionConstants.SPACE)) {
            Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
        }
    }

}
