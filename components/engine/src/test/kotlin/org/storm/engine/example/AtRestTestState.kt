package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.context.YAML_MAPPER
import org.storm.core.extensions.units
import org.storm.core.input.action.ActionState
import org.storm.core.sound.Sound
import org.storm.core.sound.SoundManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.PhysicsEngine
import org.storm.physics.entity.Entity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle

class AtRestTestState : SwitchableState() {

    private val gravity = Direction.SOUTH.vector.scale(25.0.units)
    private val resolution = Context.RESOLUTION_IN_UNITS

    private val platform: Entity = ImmovableRectEntity(
        1.0.units,
        resolution.height - 20.units,
        resolution.width - 2.units,
        10.0.units
    )

    private val repellingBall: Entity = EntityImpl(
        AABB(
            75.0.units,
            400.0.units,
            20.0.units,
            20.0.units
        ),
        2.0.units,
        10.0,
        0.7
    ).also {
        it.addForce(gravity)
    }

    private val repellingBall2: Entity = EntityImpl(
        Circle(
            150.0.units,
            50.0.units,
            20.0.units
        ),
        2.0.units,
        10.0,
        0.5
    ).also {
        it.addForce(gravity)
    }

    private val repellingBall3: Entity = EntityImpl(
        AABB(
            225.0.units,
            200.0.units,
            20.0.units,
            20.0.units
        ),
        2.0.units,
        10.0,
        1.0
    ).also {
        it.addForce(gravity)
    }

    private val repellingBall4: Entity = EntityImpl(
        AABB(
            300.0.units,
            100.0.units,
            20.0.units,
            20.0.units
        ),
        2.0.units,
        10.0,
        0.2
    ).also {
        it.addForce(gravity)
    }

    override val entities: Set<Entity> = setOf(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4)

    override suspend fun onRegister(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        val bgm = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/sound/bgm.yml"), Sound::class.java)
        soundManager.add("bgm", bgm)
        soundManager.adjustAllVolume(0.1)
    }

    override suspend fun onSwapOff(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        soundManager.stop("bgm")
    }

    override suspend fun onSwapOn(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        soundManager.play("bgm")
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)
        if (actionState.isFirstActivation(KeyActionConstants.SPACE)) {
            Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
        }
    }
}
