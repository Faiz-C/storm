package org.storm.core.exception

/**
 * An AssetException is thrown when an error occurs while dealing with assets.
 */
class AssetException(msg: String, throwable: Throwable? = null) : RuntimeException(msg, throwable)
