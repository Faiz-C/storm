package org.storm.engine.request.types

import org.storm.engine.StormEngine
import org.storm.engine.request.Request
import org.storm.physics.entity.Entity

/**
 * A Request to change the list of entities that the PhysicsEngine is using.
 */
class EntityAlterationRequest(
    private val newEntities: Set<Entity>
) : Request {

    override fun execute(stormEngine: StormEngine) {
        stormEngine.physicsEngine.entities = newEntities
    }

}
