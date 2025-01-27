package org.storm.engine.state

import org.storm.core.input.action.ActionStateProcessor
import org.storm.core.render.Renderable
import org.storm.core.render.canvas.Canvas
import org.storm.core.update.Updatable
import org.storm.physics.PhysicsEngine
import org.storm.physics.entity.Entity
import org.storm.sound.manager.SoundManager

interface GameState: Updatable, Renderable, ActionStateProcessor {

    val entities: Set<Entity>

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
        this.entities.forEach { it.render(canvas, x, y) }
    }

}