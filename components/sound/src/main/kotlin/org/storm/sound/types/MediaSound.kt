package org.storm.sound.types

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import org.slf4j.LoggerFactory
import org.storm.sound.Sound
import org.storm.sound.exception.SoundException
import java.nio.file.Paths

/**
 * A Sound implementation which uses a Javax MediaPlayer
 */
class MediaSound(
  path: String,
  loop: Boolean = false,
  resource: Boolean = true,
  private val loopCount: Int? = null,
) : Sound {

  companion object {
    private val logger = LoggerFactory.getLogger(MediaSound::class.java)
  }

  private val sound: MediaPlayer = run {
    val uri = if (resource) {
      logger.debug("loading sound file $path from resources")
      javaClass.classLoader.getResource(path)
        ?: throw SoundException("failed to find $path in resources")
    } else {
      logger.debug("loading sound file $path from file system")
      Paths.get(path).toUri()
    }

    MediaPlayer(Media(uri.toString())).also {
      it.cycleCount = if (loop) MediaPlayer.INDEFINITE else loopCount ?: 1
      it.onError = Runnable { logger.error("error occurred with background music") }
    }
  }

  override fun play() {
    sound.play()
  }

  override fun pause() {
    sound.pause()
  }

  override fun stop() {
    sound.stop()
  }

  override fun adjustVolume(volume: Double) {
    sound.volume = volume
  }
}