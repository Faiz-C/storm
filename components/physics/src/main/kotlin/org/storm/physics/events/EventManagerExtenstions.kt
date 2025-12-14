package org.storm.physics.events

import org.storm.core.event.EventManager
import org.storm.core.event.EventStream

fun EventManager.getCollisionEventStream(): EventStream<CollisionEvent> {
    return try {
        getEventStream("collision-events")
    } catch (_: IllegalArgumentException) {
        createEventStream("collision-events")
    }
}