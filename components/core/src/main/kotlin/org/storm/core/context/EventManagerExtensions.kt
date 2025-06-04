package org.storm.core.context

import org.storm.core.event.EventManager
import org.storm.core.event.EventStream

fun EventManager.getContextEventStream(): EventStream<ContextEvent> {
    return try {
        getEventStream("context-events")
    } catch (_: IllegalArgumentException) {
        createEventStream("context-events")
    }
}
