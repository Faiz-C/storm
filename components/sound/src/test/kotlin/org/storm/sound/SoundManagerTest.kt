package org.storm.sound

import javafx.application.Application
import javafx.stage.Stage
import org.storm.sound.manager.SoundManager
import org.storm.sound.types.MediaSound

class SoundManagerTest : Application() {

    companion object {
        // Add your own sound files to test. Not adding my own for obvious reasons.
        private const val BGM_FILE = "bgm.mp3"
        private const val BGM2_FILE = "bgm2.mp3"
        private const val SOUND_EFFECT_FILE = "effect.mp3"
    }

    private val soundManager = SoundManager()

    override fun start(stage: Stage) {
        soundManager.add("effect", MediaSound(SOUND_EFFECT_FILE, resource = true))
        soundManager.add("bgm1", MediaSound(BGM_FILE, loops = Sound.LOOP_INDEFINITELY, resource = true))
        soundManager.add("bgm2", MediaSound(BGM2_FILE, resource = true))
        soundManager.adjustVolume("effect", 0.5)
        soundManager.adjustVolume("bgm1", 0.1)
        soundManager.adjustVolume("bgm2", 0.3)
        soundManager.play("bgm1")
        soundManager.play("bgm2")
        soundManager.play("effect")
    }

}
