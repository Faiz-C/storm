package org.storm.engine.request.types

import org.storm.core.sound.SoundManager
import org.storm.engine.StormEngine
import org.storm.engine.request.Request
import org.storm.physics.PhysicsEngine

/**
 * A Request which toggles the pause state of the PhysicsEngine.
 */
class TogglePhysicsRequest(
    private val paused: Boolean? = null
): Request {

    override suspend fun execute(stormEngine: StormEngine, physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        if (paused != null) {
            physicsEngine.paused = this.paused
        } else {
            physicsEngine.paused = !physicsEngine.paused
        }
    }

}
