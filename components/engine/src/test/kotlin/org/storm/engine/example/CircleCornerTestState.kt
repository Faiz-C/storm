package org.storm.engine.example

import org.storm.core.context.Context
import org.storm.core.context.YAML_MAPPER
import org.storm.core.extensions.units
import org.storm.core.input.action.ActionState
import org.storm.core.sound.Sound
import org.storm.core.sound.SoundManager
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.TogglePhysicsRequest
import org.storm.physics.PhysicsEngine
import org.storm.physics.entity.PhysicsObject
import org.storm.physics.math.Vector
import org.storm.physics.math.geometry.shapes.Circle

class CircleCornerTestState : SwitchableState() {

    private val player: PhysicsObject = PhysicsObjectImpl(
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

    override val entities: Set<PhysicsObject> = setOf(
        ImmovableRectPhysicsObject(
            200.0.units,
            200.0.units,
            100.0.units,
            100.0.units
        ),
        player
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
        Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
    }

    override suspend fun process(actionState: ActionState) {
        super.process(actionState)

        if (actionState.isFirstActivation(KeyActionConstants.SPACE)) {
            Context.REQUEST_QUEUE.submit(TogglePhysicsRequest())
        }

        this.player.velocity = Vector.ZERO_VECTOR
        this.movementVectors.map { (action, vector) ->
            if (actionState.isActionHeld(action, 1)) {
                this.player.velocity += vector.scale(this.player.speed)
            }
        }
    }

}
