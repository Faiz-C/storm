package org.storm.core.exception

/**
 * A standard sound exception
 */
class SoundException(
    msg: String,
    e: Exception? = null
) : RuntimeException(msg, e)
