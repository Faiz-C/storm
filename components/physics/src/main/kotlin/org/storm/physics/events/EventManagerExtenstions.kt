package org.storm.physics.events

import org.storm.core.event.EventManager
import org.storm.core.event.EventStream

fun EventManager.getCollisionEventStream(): EventStream<CollisionEvent> = getOrCreate("collision-events")