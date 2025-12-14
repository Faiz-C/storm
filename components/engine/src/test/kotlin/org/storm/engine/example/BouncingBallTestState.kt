package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.context.YAML_MAPPER
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.input.ActionState
import org.storm.core.sound.Sound
import org.storm.core.sound.SoundManager
import org.storm.engine.Controls
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.PhysicsEngine
import org.storm.physics.collision.Collider
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.Circle

class BouncingBallTestState : SwitchableState() {

    private val resolution = Context.RESOLUTION_IN_UNITS

    private val rects: Set<ImmovableRect> = setOf(
        ImmovableRect(
            0.0,
            0.0,
            resolution.width,
            5.0.units
        ),
        ImmovableRect(
            0.0,
            0.0,
            5.0.units,
            resolution.height
        ),
        ImmovableRect(
            resolution.width - 5.units,
            0.0,
            5.0.units,
            resolution.height
        ),
        ImmovableRect(
            0.0,
            resolution.height - 5.units,
            resolution.width,
            5.0.units
        )
    )

    override val colliders: Set<Collider> = rects.map { it.collider }.plus(Collider(
        Circle(
            25.0.units,
            200.0.units,
            15.0.units
        ),
        100.0.units,
        1.0
    )).toSet()

    override suspend fun onRegister(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        val bgm = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/sound/bgm.yml"), Sound::class.java)
        soundManager.add("bgm", bgm)
        soundManager.adjustAllVolume(0.1)

        colliders.last().addForce(Direction.SOUTH_EAST.vector.scale(100.0.units), 0.1)
    }

    override suspend fun onSwapOn(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        soundManager.play("bgm")
    }

    override suspend fun onSwapOff(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        soundManager.pause("bgm")
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)
        if (actionState.isFirstActivation(Controls.SPACE)) {
            RequestQueue.submit(TogglePhysicsRequest())
        }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        rects.forEach {
            it.render(canvas, x, y)
        }

        colliders.last().render(canvas, x, y)
    }
}
