package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.context.YAML_MAPPER
import org.storm.core.event.EventManager
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.input.ActionState
import org.storm.core.sound.Sound
import org.storm.core.sound.SoundManager
import org.storm.engine.Controls
import org.storm.engine.events.EngineEvent
import org.storm.engine.events.getEngineEventStream
import org.storm.physics.PhysicsEngine
import org.storm.physics.collision.Collider
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.Circle

class CircleCornerTestState : SwitchableState() {

    private data class Player(var speed: Double) {
        val collider = Collider(
            Circle(
                25.0.units,
                200.0.units,
                10.0.units
            ),
            100.0.units,
            0.1,
            this
        )
    }

    private val player = Player(2.0.units)
    private val rect = ImmovableRect(
        200.0.units,
        200.0.units,
        100.0.units,
        100.0.units
    )

    private val movementVectors = mapOf(
        Controls.W to Vector.UNIT_NORTH,
        Controls.S to Vector.UNIT_SOUTH,
        Controls.A to Vector.UNIT_WEST,
        Controls.D to Vector.UNIT_EAST,
    )

    override val colliders: Set<Collider> = setOf(
        rect.collider,
        player.collider
    )

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
        EventManager.getEngineEventStream().produce(EngineEvent.TogglePhysics())
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)

        if (actionState.isFirstActivation(Controls.SPACE)) {
            EventManager.getEngineEventStream().produce(EngineEvent.TogglePhysics())
        }

        // TODO: this is kinda jank, should update eventually
        this.player.collider.velocity = Vector.ZERO_VECTOR
        this.movementVectors.map { (action, vector) ->
            if (actionState.isActionHeld(action, 1)) {
                this.player.collider.velocity += vector.scale(this.player.speed)
            }
        }
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        rect.render(canvas, x, y)
        player.collider.render(canvas, x, y)
    }

}
