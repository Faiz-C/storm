package org.storm.core.sound

import javafx.application.Application
import javafx.stage.Stage
import org.storm.core.context.Context
import org.storm.core.context.YAML_MAPPER
import org.storm.core.context.loadMappers
import org.storm.core.context.setMasterVolume

class SoundManagerTest : Application() {

    private val soundManager = SoundManager()

    override fun start(stage: Stage) {
        Context.loadMappers()

        val bgm1 = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/sound/bgm1.yml"), Sound::class.java)
        val bgm2 = Context.YAML_MAPPER.readValue(this::class.java.getResourceAsStream("/sound/bgm2.yml"), Sound::class.java)

        soundManager.add("bgm1", bgm1)
        soundManager.add("bgm2", bgm2)

        Context.setMasterVolume(0.8)

        soundManager.adjustVolume("bgm1", 0.1)
        soundManager.adjustVolume("bgm2", 0.3)
        soundManager.play("bgm1")
        soundManager.play("bgm2")
    }

}