package org.storm.sound.types

import javafx.application.Application
import javafx.stage.Stage
import org.storm.sound.Sound

class JfxSoundTest : Application() {

    companion object {
        private const val BGM_FILE = "src/test/resources/music/bgm.mp3"
    }

    private lateinit var bgm: Sound

    override fun start(stage: Stage) {
        bgm = JfxSound(BGM_FILE)
        bgm.adjustVolume(0.1)
        bgm.play()
    }

}
