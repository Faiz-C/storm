package org.storm.sound

import javafx.application.Application
import javafx.stage.Stage
import org.storm.sound.manager.SoundManager
import org.storm.sound.types.MediaSound

class SoundManagerTest : Application() {

  companion object {
    // Add your own sound files to test. Not adding my own for obvious reasons.
    private const val BGM_FILE = "src/test/resources/bgm.mp3"
    private const val BGM2_FILE = "src/test/resources/bgm2.mp3"
    private const val SOUND_EFFECT_FILE = "src/test/resources/effect.mp3"
  }

  private val soundManager = SoundManager()

  override fun start(stage: Stage) {
    soundManager.add("effect", MediaSound(SOUND_EFFECT_FILE, false))
    soundManager.add("bgm1", MediaSound(BGM_FILE, true))
    soundManager.add("bgm2", MediaSound(BGM2_FILE, false))
    soundManager.adjustVolume("effect", 0.5)
    soundManager.adjustVolume("bgm1", 0.1)
    soundManager.adjustVolume("bgm2", 0.3)
    soundManager.play("bgm1")
    soundManager.play("bgm2")
    soundManager.play("effect")
  }

}
