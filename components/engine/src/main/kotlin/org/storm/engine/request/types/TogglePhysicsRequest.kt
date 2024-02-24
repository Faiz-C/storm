package org.storm.engine.request.types

import org.storm.engine.StormEngine
import org.storm.engine.request.Request

/**
 * A Request which pauses or unpauses the PhysicsEngine.
 */
class TogglePhysicsRequest(
    private val paused: Boolean
) : Request {

    override fun execute(stormEngine: StormEngine) {
        stormEngine.physicsEngine.paused = paused
    }

}
