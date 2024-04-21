package org.storm.engine

import javafx.application.Application
import javafx.stage.Stage
import org.storm.core.asset.AssetManager
import org.storm.core.asset.source.types.LocalStorageAssetSource
import org.storm.engine.example.*
import org.storm.physics.ImpulseResolutionPhysicsEngine
import org.storm.sound.manager.SoundManager
import java.nio.file.Paths

class StormEngineTest : Application() {

    override fun start(primaryStage: Stage) {
        val stormEngine = StormEngine(
            physicsEngine = ImpulseResolutionPhysicsEngine(),
            renderFps = 144,
            logicFps = 240,
        )

        val assetManager = AssetManager().also {
            val resourceDir = Paths.get("src", "test", "resources")

            it.registerSource(
                LocalStorageAssetSource(resourceDir.toString())
            )
        }

        stormEngine.addState(KeyActionConstants.ONE, AtRestTestState(assetManager))
        stormEngine.addState(KeyActionConstants.TWO, BouncingBallTestState(assetManager))
        stormEngine.addState(KeyActionConstants.THREE, ParticleTestState(assetManager))
        stormEngine.addState(KeyActionConstants.FOUR, CircleCornerTestState(assetManager))

        stormEngine.addKeyTranslator(ActionTranslatorImpl())
        stormEngine.fpsChangeAllow = false

        stormEngine.swapState(KeyActionConstants.THREE)
        stormEngine.run()

        primaryStage.scene = stormEngine.window
        primaryStage.show()
    }

}
