package org.storm.engine.request.types

import org.storm.engine.StormEngine
import org.storm.engine.request.Request
import org.storm.physics.PhysicsEngine
import org.storm.sound.manager.SoundManager

/**
 * A Request which toggles the pause state of the PhysicsEngine.
 */
class TogglePhysicsRequest: Request {

    override suspend fun execute(stormEngine: StormEngine, physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        physicsEngine.paused = !physicsEngine.paused
    }

}
