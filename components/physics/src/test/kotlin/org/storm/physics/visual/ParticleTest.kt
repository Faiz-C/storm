package org.storm.physics.visual

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.context.setResolution
import org.storm.core.extensions.units
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Color
import org.storm.core.render.canvas.Settings
import org.storm.core.render.geometry.Point
import org.storm.core.ui.Resolution
import org.storm.core.ui.JfxWindow
import org.storm.physics.entity.Entity
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import java.util.concurrent.ThreadLocalRandom

class ParticleTest : Application() {

    private val boundingBox: ImmovableEntity
    private val entities: MutableSet<Entity> = mutableSetOf()

    private lateinit var physicsSimulator: PhysicsSimulator
    private val ballColour = Color(
        ThreadLocalRandom.current().nextDouble(255.0),
        ThreadLocalRandom.current().nextDouble(255.0),
        ThreadLocalRandom.current().nextDouble(255.0),
        1.0
    )

    init {
        Context.setResolution(Resolution.HD)
        val resolution = Context.RESOLUTION_IN_UNITS
        boundingBox = ImmovableEntity(
            mutableMapOf(
                "platformTop" to AABB(
                    0.0,
                    0.0,
                    resolution.width,
                    5.0.units
                ),
                "platformRight" to AABB(
                    0.0,
                    0.0,
                    5.0.units,
                    resolution.height
                ),
                "platformBottom" to AABB(
                    resolution.width - 5.units,
                    0.0,
                    5.0.units,
                    resolution.height.units
                ),
                "platformLeft" to AABB(
                    0.0,
                    resolution.height - 5.units,
                    resolution.width,
                    5.0.units
                )
            )
        )
    }

    override fun start(stage: Stage) {
        // Make a Display
        val resolution = Context.RESOLUTION
        val window = JfxWindow()
        this.physicsSimulator = PhysicsSimulator(144.0) { render(window) }
        this.entities.add(boundingBox)

        for (i in 0..999) {
            val (x, y) = Point(
                ThreadLocalRandom.current().nextInt(10, (resolution.width - 10.units).toInt())
                    .toDouble(),
                ThreadLocalRandom.current().nextInt(10, (resolution.height - 10.units).toInt())
                    .toDouble()
            )
            this.entities.add(SimpleEntity(Circle(x, y, 2.0.units), 3.0, 0.5, 1.0))
        }

        this.physicsSimulator.physicsEngine.entities = this.entities

        this.entities.forEach {
            it.addForce(Direction.random().vector.scale(2.0.units), 0.1)
        }

        this.physicsSimulator.physicsEngine.paused = true

        window.onKeyPressed = EventHandler { keyEvent: KeyEvent ->
            if (keyEvent.code == KeyCode.SPACE) {
                this.physicsSimulator.physicsEngine.paused = !this.physicsSimulator.physicsEngine.paused
            }
        }

        this.physicsSimulator.simulate()
        stage.scene = window
        stage.show()
    }

    private suspend fun render(window: JfxWindow) {
        window.canvas.clear()
        this.physicsSimulator.physicsEngine.render(window.canvas, 0.0, 0.0)
        this.entities.forEach { e: Entity ->
            val canvasSettings = if (e is ImmovableEntity) {
                Settings(color = Color(255.0, 0.0, 0.0, 1.0))
            } else {
                Settings(color = ballColour)
            }

            window.canvas.withSettings(canvasSettings) {
                e.render(it, 0.0, 0.0)
            }
        }
    }
}
