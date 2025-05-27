package org.storm.engine.state

import org.storm.core.input.action.ActionStateProcessor
import org.storm.core.graphics.Renderable
import org.storm.core.graphics.canvas.Canvas
import org.storm.core.sound.SoundManager
import org.storm.core.update.Updatable
import org.storm.physics.PhysicsEngine
import org.storm.physics.collision.Collider

interface GameState: Updatable, Renderable, ActionStateProcessor {

    val colliders: Set<Collider>

    suspend fun onRegister(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        // Default behaviour is a noop
    }

    suspend fun onUnregister(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        // Default behaviour is a noop
    }

    suspend fun onSwapOff(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        // Default behaviour is a noop
    }

    suspend fun onSwapOn(physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        // Default behaviour is a noop
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        // Default behaviour is a noop
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        this.colliders.forEach { it.render(canvas, x, y) }
    }

}