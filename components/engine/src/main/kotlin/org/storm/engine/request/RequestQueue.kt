package org.storm.engine.request

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A RequestQueue is a simple non-blocking queue for queuing Requests for the StormEngine to action on at the start
 * of the next game loop iteration.
 */
class RequestQueue {

    private val queue: Queue<List<Request>> = ConcurrentLinkedQueue()

    /**
     * Submits the given Request to the queue. The request will be executed at the start of the next game
     * loop iteration.
     *
     * @param request Request to submit
     */
    fun submit(request: Request) {
        this.queue.add(listOf(request))
    }

    /**
     * Submits the given Requests to the queue as a list. These will all be executed together and in the order given
     * at the start of the next game loop iteration.
     *
     * @param requests variable number of Requests to submit
     */
    fun submit(vararg requests: Request) {
        this.queue.add(requests.toList())
    }

    /**
     * Clears the queue
     */
    fun clear() {
        this.queue.clear()
    }

    /**
     * This is a non-blocking retrieve of the next request in the queue.
     *
     * @return The next Request in the queue or null if the queue is empty
     */
    operator fun next(): List<Request>? {
        return this.queue.poll()
    }

}
