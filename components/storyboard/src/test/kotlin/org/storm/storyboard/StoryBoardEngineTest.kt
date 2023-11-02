package org.storm.storyboard

import javafx.scene.canvas.Canvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

class StoryBoardEngineTest {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val resourceDir = Paths.get("components", "storyboard", "src", "test", "resources", "states")
            val engine = StoryBoardEngine(resourceDir.toString())

            engine.loadStatesFrom("concert")
            engine.setStartingState("concert")

            val graphics = Canvas().graphicsContext2D

            runBlocking {
                while (this.isActive) {
                    engine.update(0.0, 0.0)
                    engine.render(graphics, 0.0, 0.0)

                    delay(2000)
                }
            }
        }
    }
}
