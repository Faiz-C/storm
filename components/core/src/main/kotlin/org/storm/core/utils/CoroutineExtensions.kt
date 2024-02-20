package org.storm.core.utils

import kotlinx.coroutines.*

fun CoroutineScope.onInterval(interval: Long, block: suspend () -> Unit): Job {
  return launch {
    while (this.isActive) {
      block()
      delay(interval)
    }
  }
}
