package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.context.YAML_MAPPER
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.input.action.ActionState
import org.storm.core.sound.Sound
import org.storm.core.sound.SoundManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.PhysicsEngine
import org.storm.physics.collision.Collider
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle

class AtRestTestState : SwitchableState() {

    private val gravity = Direction.SOUTH.vector.scale(25.0.units)
    private val resolution = Context.RESOLUTION_IN_UNITS

    private val platform = ImmovableRect(
        1.0.units,
        resolution.height - 20.units,
        resolution.width - 2.units,
        10.0.units
    )

    private val repellingBall: Collider = Collider(
        AABB(
            75.0.units,
            400.0.units,
            20.0.units,
            20.0.units
        ),
        100.0.units,
        0.7
    )

    private val repellingBall2: Collider = Collider(
        Circle(
            150.0.units,
            50.0.units,
            20.0.units
        ),
        100.0.units,
        0.5
    )

    private val repellingBall3: Collider = Collider(
        AABB(
            225.0.units,
            200.0.units,
            20.0.units,
            20.0.units
        ),
        100.0.units,
        1.0
    )

    private val repellingBall4: Collider = Collider(
        AABB(
            300.0.units,
            100.0.units,
            20.0.units,
            20.0.units
        ),
        100.0.units,
        0.2
    )

    override val colliders: Set<Collider> = setOf(platform.collider, repellingBall, repellingBall2, repellingBall3, repellingBall4)

    override suspend fun onRegister(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        val bgm = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/sound/bgm.yml"), Sound::class.java)
        soundManager.add("bgm", bgm)
        soundManager.adjustAllVolume(0.1)

        physicsEngine.applyForce(gravity, repellingBall)
        physicsEngine.applyForce(gravity, repellingBall2)
        physicsEngine.applyForce(gravity, repellingBall3)
        physicsEngine.applyForce(gravity, repellingBall4)
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

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        platform.render(canvas, x, y)
        colliders.drop(1).forEach {
            it.render(canvas, x, y)
        }
    }
}
