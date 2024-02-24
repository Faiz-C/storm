package org.storm.storyboard.exception

/**
 * Standard exception thrown when an error occurs during storyboard management
 */
class StoryBoardEngineException(msg: String, th: Throwable? = null) : RuntimeException(msg, th)

