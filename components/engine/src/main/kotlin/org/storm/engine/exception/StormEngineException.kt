package org.storm.engine.exception

/**
 * Standard Runtime Exception used for issues within the StormEngine
 */
class StormEngineException(
  msg: String,
  e: Exception? = null
) : RuntimeException(msg, e)
