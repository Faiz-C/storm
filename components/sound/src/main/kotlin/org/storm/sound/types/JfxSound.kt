package org.storm.sound.types

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import org.slf4j.LoggerFactory
import org.storm.core.serialization.Polymorphic
import org.storm.sound.Sound
import org.storm.sound.exception.SoundException
import java.net.URI
import java.nio.file.Paths

/**
 * A Sound implementation which uses a JavaFx MediaPlayer
 */
@Polymorphic("jfx-sound")
class JfxSound(
    file: String = "",
    url: String = "",
    override val delay: Int = 0,
    override val loops: Int = 1,
    override val soundType: String = Sound.Type.BGM.value
) : Sound {

    companion object {
        private val logger = LoggerFactory.getLogger(JfxSound::class.java)
    }

    private var hasCompletedOnce = false

    private val sound: MediaPlayer = run {
        val uri = when {
            file.isNotBlank() -> Paths.get(file).toUri()
            url.isNotBlank() -> URI(url)
            else -> throw SoundException("Must provide file path or url to load sound")
        }

        MediaPlayer(Media(uri.toString())).also {
            it.cycleCount = if (loops == Sound.LOOP_INDEFINITELY) MediaPlayer.INDEFINITE else loops
            it.onRepeat = Runnable { hasCompletedOnce = true }
            it.onError = Runnable { logger.error("error occurred with sound from uri $uri") }
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
