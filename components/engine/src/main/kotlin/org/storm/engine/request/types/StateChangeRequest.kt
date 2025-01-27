package org.storm.engine.request.types

import org.storm.engine.StormEngine
import org.storm.engine.request.Request
import org.storm.physics.PhysicsEngine
import org.storm.sound.manager.SoundManager

/**
 * A Request which changes the current state of the StormEngine.
 */
class StateChangeRequest(
    private val stateId: String
) : Request {

    override suspend fun execute(stormEngine: StormEngine, physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        stormEngine.swapState(stateId)
    }

}
