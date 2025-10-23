package org.storm.engine.request

import org.storm.core.sound.SoundManager
import org.storm.engine.StormEngine
import org.storm.physics.PhysicsEngine

/**
 * Represents a request which can be sent to the StormEngine to handle. Requests are carried out before
 * the logic stage of the engine loop.
 */
fun interface Request {

    /**
     * Executes the Request using the given StormEngine
     *
     * @param stormEngine instance of the StormEngine in use
     * @param physicsEngine instance of the PhysicsEngine in use
     * @param soundManager instance of the SoundManager in use
     */
    suspend fun execute(stormEngine: StormEngine, physicsEngine: PhysicsEngine, soundManager: SoundManager)

}
