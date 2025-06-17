package org.storm.core.utils

/**
 * @param nanoSeconds time (in nanoseconds) to convert to seconds
 * @return the given nanoseconds in seconds
 */
fun toSeconds(nanoSeconds: Long): Double {
    return nanoSeconds / 1_000_000_000.0
}

/**
 * @param nanoSeconds time (in nanoseconds) to convert to milliseconds
 * @return the given nanoseconds in milliseconds
 */
fun toMilliseconds(nanoSeconds: Long): Double {
    return nanoSeconds / 1_000_000.0
}