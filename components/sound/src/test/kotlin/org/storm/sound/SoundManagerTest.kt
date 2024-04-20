package org.storm.sound

import javafx.application.Application
import javafx.stage.Stage
import org.storm.core.asset.AssetManager
import org.storm.core.asset.source.loaders.localstorage.YamlLocalStorageAssetLoader
import org.storm.core.asset.source.types.LocalStorageAssetSource
import org.storm.core.context.Context
import org.storm.sound.context.setMasterVolume
import org.storm.sound.manager.SoundManager
import java.nio.file.Paths

class SoundManagerTest : Application() {

    private val assetManager = AssetManager().also {
        val resourceDir = Paths.get("src", "test", "resources")

        it.registerSource(
            LocalStorageAssetSource(
                resourceDir.toString(),
                listOf(
                    YamlLocalStorageAssetLoader()
                )
            )
        )
    }

    private val soundManager = SoundManager(assetManager = assetManager)

    override fun start(stage: Stage) {
        soundManager.loadSound("effect")
        soundManager.loadSound("bgm1")
        soundManager.loadSound("bgm2")

        Context.setMasterVolume(0.8)

        soundManager.adjustVolume("effect", 0.5)
        soundManager.adjustVolume("bgm1", 0.1)
        soundManager.adjustVolume("bgm2", 0.3)
        soundManager.play("bgm1")
        soundManager.play("bgm2")
        soundManager.play("effect")
    }

}
