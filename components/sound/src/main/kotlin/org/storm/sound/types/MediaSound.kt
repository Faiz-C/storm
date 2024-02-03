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
  resource: Boolean = true,
  override val delay: Int = 0,
  override val loops: Int = 1,
) : Sound {

  companion object {
    private val logger = LoggerFactory.getLogger(MediaSound::class.java)
  }

  private var hasCompletedOnce = false

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
      it.cycleCount = if (loops == Sound.LOOP_INDEFINITELY) MediaPlayer.INDEFINITE else loops
      it.onRepeat = Runnable { hasCompletedOnce = true }
      it.onError = Runnable { logger.error("error occurred with sound") }
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

  override fun isComplete(): Boolean {
    return sound.status == MediaPlayer.Status.STOPPED || (loops == Sound.LOOP_INDEFINITELY && hasCompletedOnce)
  }
}
