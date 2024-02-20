package org.storm.core.input

data class ActionInputInfo(
  val triggers: Int = 1,
  val activeSnapshots: Int = 0,
  val inDebounce: Boolean = false,
  val timeHeldInMillis: Long,
  val lastUpdateTime: Long
)
