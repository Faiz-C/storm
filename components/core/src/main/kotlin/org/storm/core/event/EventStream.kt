package org.storm.core.event

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Represents a stream of events of type [T].
 *
 * Events can be produced asynchronously and are queued until processed during the engine's update phase.
 * Consumers (handlers) are called synchronously during [process].
 *
 * @param id Unique identifier for this event stream.
 */
class EventStream<T> internal constructor(val id: String): AutoCloseable {

    companion object {
        private val logger = LoggerFactory.getLogger(EventStream::class.java)
    }

    /**
     * Thread-safe queue of pending events to be processed during [process].
     */
    private val pendingEvents: Queue<T> = ConcurrentLinkedQueue()
    private val listeners = CopyOnWriteArrayList<suspend (T) -> Unit>() // Thread safe list that is good for minimum writes

    /**
     * Produces an event and adds it to the queue to be processed during [process].
     * Thread-safe.
     *
     * This method suspends until the event is enqueued. The event will be handled
     * during the next call to [process].
     *
     * @param event The event to produce.
     */
    fun produce(event: T) {
        pendingEvents.add(event)
    }

    /**
     * Adds a consumer (event handler) to this stream.
     * Thread-safe.
     *
     * The consumer will be called for each event during [process].
     *
     * @param listener The suspend function to handle events.
     * @return A lambda that closes the listener
     */
    fun subscribe(listener: suspend (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return { listeners.remove(listener) }
    }

    /**
     * Processes all queued events and calls all consumers synchronously for each event.
     * This is called during the engine's update phase to ensure event handlers are invoked
     * in sync with the game loop.
     *
     * This method is thread-safe and drains the event queue, ensuring no events are missed.
     *
     * WARNING:
     * If new events are produced during processing, they will also be processed before returning.
     * Ensure you are not looping events.
     *
     */
    internal suspend fun process() {
        while (true) {
            val event = pendingEvents.poll() ?: break
            notify(event)
        }
    }

    override fun close() {
        this.pendingEvents.clear()
        this.listeners.clear()
    }

    /**
     * Calls all consumers for the given event, catching and logging exceptions per consumer.
     * @param event The event to notify consumers about.
     */
    private suspend fun notify(event: T) {
        this.listeners.forEach {
            try {
                it(event)
            } catch (e: Exception) {
                logger.warn("Encountered issue with listener when listening to event", e)
            }
        }
    }

}