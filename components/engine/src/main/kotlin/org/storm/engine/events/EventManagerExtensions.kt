package org.storm.engine.events

import org.storm.core.event.EventManager
import org.storm.core.event.EventStream

internal const val ENGINE_EVENT_STREAM_ID = "engine-events"

fun EventManager.getEngineEventStream(): EventStream<EngineEvent> = getOrCreate(ENGINE_EVENT_STREAM_ID)