package org.storm.core.extensions

import kotlinx.coroutines.*

fun CoroutineScope.scheduleOnInterval(interval: Long, block: suspend () -> Unit): Job {
    return launch {
        while (this.isActive) {
            block()
            delay(interval)
        }
    }
}
