package org.storm.core.event

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The EventManager is responsible for the async setup and tear down of EventStreams used within the game engine.
 */
object EventManager: AutoCloseable {
    private val eventStreams = ConcurrentHashMap<String, EventStream<*>>()

    /**
     * Creates, stores and returns an EventStream for the specified generic type T.
     *
     * @param eventStreamId Unique id for the event stream
     * @throws IllegalArgumentException When an EventStream already exists for the given id
     * @return An EventStream for the wanted generic type T
     */
    fun <T> createEventStream(eventStreamId: String = UUID.randomUUID().toString()): EventStream<T> {
        require(!eventStreams.containsKey(eventStreamId)) {
            "Event id $eventStreamId is already in use"
        }

        return EventStream<T>(eventStreamId).also {
            eventStreams[eventStreamId] = it
        }
    }

    /**
     * Removes the EventStream associated with the given id if it exists
     *
     * @param eventStreamId Unique id of the event stream
     */
    fun removeEventStream(eventStreamId: String) {
        eventStreams.remove(eventStreamId)
    }

    /**
     * Returns the EventStream for the given id. **Note**: This method performs an unchecked cast on the found EventStream
     * to the given generic type.
     *
     * @param eventStreamId Unique id of this event stream
     * @throws IllegalArgumentException When no EventStream exists for the given id
     * @return The EventStream for the given id
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getEventStream(eventStreamId: String): EventStream<T> {
        require(eventStreams.containsKey(eventStreamId)) {
            "Event with id $eventStreamId not found"
        }

        return eventStreams[eventStreamId]!! as EventStream<T>
    }

    suspend fun processEvents() {
        eventStreams.values.forEach { stream ->
            stream.process()
        }
    }

    override fun close() {
        eventStreams.forEach {
            it.value.close()
        }
    }
}