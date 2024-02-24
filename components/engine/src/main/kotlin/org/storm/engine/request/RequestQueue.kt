package org.storm.engine.request

import org.storm.engine.exception.StormEngineException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A RequestQueue is a simple non-blocking queue (backed by a ConcurrentLinkedQueue) for queuing a list of Requests
 * and retrieving them.
 */
class RequestQueue(
    private val maxRequestSize: Int = UNLIMITED_REQUEST_SIZE
) {

    companion object {
        const val UNLIMITED_REQUEST_SIZE = -1
    }

    private val queue: Queue<List<Request>> = ConcurrentLinkedQueue()

    /**
     * Submits the given list of Requests to the queue
     *
     * @param requests list of Requests to submit
     */
    fun submit(requests: List<Request>) {
        if (requests.size > maxRequestSize && maxRequestSize != UNLIMITED_REQUEST_SIZE) {
            throw StormEngineException("single request size higher than allowed limit")
        }
        this.queue.add(requests)
    }

    /**
     * Submits the given Requests to the queue as a single list
     *
     * @param requests variable number of Requests to submit
     */
    fun submit(vararg requests: Request) {
        this.submit(listOf(*requests))
    }

    /**
     * Submits the given Requests to the queue separately
     *
     * @param requests variable number of Requests to submit
     */
    fun submitSeparately(vararg requests: Request) {
        requests.forEach {
            this.submit(it)
        }
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
     * @return an Optional with either the new list of requests in the queue or empty if the queue is empty
     */
    operator fun next(): List<Request>? {
        return this.queue.poll()
    }

}
