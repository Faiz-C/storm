package org.storm.core.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

class EventStream<T> internal constructor(val id: String): AutoCloseable {
    companion object {
        private val logger = LoggerFactory.getLogger(EventStream::class.java)
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(4))

    private val queue = Channel<T>(Channel.UNLIMITED)
    private val consumers = mutableListOf<suspend (T) -> Unit>()
    private val consumerMutex = Mutex()

    fun produceAsync(event: T) {
        coroutineScope.launch {
            produce(event)
        }
    }

    suspend fun produce(event: T) {
        queue.send(event)
    }

    fun addConsumerAsync(consumer: suspend (T) -> Unit) {
        coroutineScope.launch {
            addConsumer(consumer)
        }
    }

    suspend fun addConsumer(consumer: suspend (T) -> Unit) {
        consumerMutex.withLock {
            consumers.add(consumer)
        }
    }

    internal suspend fun start() {
        for (event in queue) {
            notify(event)
        }
    }

    internal suspend fun consume(): T {
        val event = queue.receive()
        notify(event)
        return event
    }

    private suspend fun notify(event: T) {
        consumerMutex.withLock {
            consumers.forEach {
                try {
                    it(event)
                } catch (e: Exception) {
                    logger.warn("Encountered issue with an EventHandler when listening to event", e)
                }
            }
        }
    }

    override fun close() {
        queue.close()

        if (coroutineScope.isActive) {
            coroutineScope.cancel("Closing Event Manager")
        }
    }
}