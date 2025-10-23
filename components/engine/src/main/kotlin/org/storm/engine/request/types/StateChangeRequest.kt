package org.storm.engine.request.types

import org.storm.core.sound.SoundManager
import org.storm.engine.StormEngine
import org.storm.engine.exception.StormEngineException
import org.storm.engine.request.Request
import org.storm.engine.state.GameState
import org.storm.physics.PhysicsEngine

/**
 * A Request which changes the current state of the StormEngine.
 */
class StateChangeRequest(
    private val stateId: String? = null,
    private val state: GameState? = null
) : Request {

    override suspend fun execute(stormEngine: StormEngine, physicsEngine: PhysicsEngine, soundManager: SoundManager) {
        if (stateId != null) {
            stormEngine.swapState(this.stateId)
        } else if (state != null) {
            stormEngine.swapState(this.state)
        } else {
            throw StormEngineException("One of `stateId` or `state` must not be null")
        }
    }
}
