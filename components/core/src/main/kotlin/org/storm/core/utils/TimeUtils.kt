package org.storm.core.utils

object TimeUtils {
    /**
     * @param nanoSeconds time (in nanoseconds) to convert to seconds
     * @return the given nanoseconds in seconds
     */
    fun toSeconds(nanoSeconds: Long): Double {
        return nanoSeconds / 1000000000.0
    }
}
