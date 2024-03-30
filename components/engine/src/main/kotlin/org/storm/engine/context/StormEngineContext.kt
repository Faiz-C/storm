package org.storm.engine.context

import org.storm.core.context.Context
import org.storm.engine.request.RequestQueue

private val requestQueue: RequestQueue = RequestQueue()

/**
 * @return The request queue used to manage requests to the StormEngin.
 */
val Context.REQUEST_QUEUE: RequestQueue get() = requestQueue
