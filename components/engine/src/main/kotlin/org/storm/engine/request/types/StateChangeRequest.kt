package org.storm.engine.request.types

import org.storm.engine.StormEngine
import org.storm.engine.request.Request
import org.storm.physics.PhysicsEngine

/**
 * A Request which changes the current state of the StormEngine.
 */
class StateChangeRequest(
    private val stateId: String,
    private val reset: Boolean = false
) : Request {

    override fun execute(stormEngine: StormEngine, physicsEngine: PhysicsEngine) {
        stormEngine.swapState(stateId, reset)
    }

}
