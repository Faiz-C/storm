package org.storm.engine.request.types

import org.storm.engine.StormEngine
import org.storm.engine.request.Request

/**
 * A Request which toggles the pause state of the PhysicsEngine.
 */
class TogglePhysicsRequest: Request {

    override fun execute(stormEngine: StormEngine) {
        stormEngine.physicsEngine.paused = !stormEngine.physicsEngine.paused
    }

}
