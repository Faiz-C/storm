package org.storm.core.event

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

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

    private val coroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(4))

    /**
     * Thread-safe queue of pending events to be processed during [process].
     */
    private val pendingEvents: Queue<T> = ConcurrentLinkedQueue()
    private val consumers = mutableListOf<suspend (T) -> Unit>()
    private val consumerMutex = Mutex()

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
     * Asynchronously adds a consumer (event handler) to this stream.
     *
     * This method launches a coroutine to add the consumer. The consumer will be called
     * for each event during [process].
     *
     * @param consumer The suspend function to handle events.
     */
    fun addConsumerAsync(consumer: suspend (T) -> Unit) {
        coroutineScope.launch {
            addConsumer(consumer)
        }
    }

    /**
     * Adds a consumer (event handler) to this stream.
     * Thread-safe.
     *
     * The consumer will be called for each event during [process].
     *
     * @param consumer The suspend function to handle events.
     */
    suspend fun addConsumer(consumer: suspend (T) -> Unit) {
        consumerMutex.withLock {
            consumers.add(consumer)
        }
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
        consumerMutex.withLock {
            while (true) {
                val event = pendingEvents.poll() ?: break
                notify(event)
            }
        }
    }

    /**
     * Calls all consumers for the given event, catching and logging exceptions per consumer.
     * @param event The event to notify consumers about.
     */
    private suspend fun notify(event: T) {
        consumers.forEach {
            try {
                it(event)
            } catch (e: Exception) {
                logger.warn("Encountered issue with an EventHandler when listening to event", e)
            }
        }
    }

    /**
     * Closes this event stream and cancels any background coroutines.
     */
    override fun close() {
        if (coroutineScope.isActive) {
            coroutineScope.cancel("Closing Event Manager")
        }
    }
}