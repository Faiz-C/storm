package org.storm.core.extensions

import kotlinx.coroutines.*
import org.storm.core.utils.toMilliseconds
import kotlin.time.Duration.Companion.milliseconds

/**
 * Runs the given [block] on the given interval *as closely as possible*.
 *
 * @param interval the interval in milliseconds to schedule on
 * @param block the lambda or code block to run on the schedule
 *
 * @return the coroutine job that is running this work
 */
fun CoroutineScope.scheduleOnInterval(interval: Long, block: suspend () -> Unit): Job {
    val intervalNanoSeconds = interval * 1_000_000L
    return launch {
        while (this.isActive) {
            // Capture the time it took to run the block
            val start = System.nanoTime()
            block()
            val elapsed = System.nanoTime() - start

            // Calculate how much time we have remaining to wait.
            // If the block ran quickly then wait until the next expected interval time, if it took too long then
            // immediately run again
            val remaining = intervalNanoSeconds - elapsed
            if (remaining > 0) {
                delay(toMilliseconds(remaining).milliseconds)
            } else {
                // Allow the underlying coroutine pool to still have the chance to utilize the thread this scheduled
                // job is using.
                yield()
            }
        }
    }
}
