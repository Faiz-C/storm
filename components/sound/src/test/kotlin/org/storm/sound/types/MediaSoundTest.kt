package org.storm.sound.types

import javafx.application.Application
import javafx.stage.Stage
import org.storm.sound.Sound

class MediaSoundTest : Application() {

    companion object {
        private const val BGM_FILE = "bgm.mp3"
    }

    private lateinit var bgm: Sound

    override fun start(stage: Stage) {
        bgm = MediaSound(BGM_FILE, resource = true)
        bgm.adjustVolume(0.1)
        bgm.play()
    }

}
