package org.storm.maps.exception

/**
 * Simple exception thrown when map layer errors occurred
 */
class MapLayerException(
  msg: String,
  th: Throwable? = null
) : RuntimeException(msg, th)
