package org.storm.physics.visual

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.context.setResolution
import org.storm.core.event.EventManager
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Color
import org.storm.core.graphics.canvas.Settings
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.Resolution
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.collision.Collider
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import java.util.concurrent.ThreadLocalRandom

class ParticleTest : Application() {

    private val boundingBox: Collider
    private val colliders: MutableSet<Collider> = mutableSetOf()

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
        boundingBox = Collider(
            mutableMapOf(
                "platformTop" to AABB(
                    0.0,
                    0.0,
                    resolution.width,
                    5.0.units
                ),
                "platformLeft" to AABB(
                    0.0,
                    0.0,
                    5.0.units,
                    resolution.height
                ),
                "platformRight" to AABB(
                    resolution.width - 5.units,
                    0.0,
                    5.0.units,
                    resolution.height
                ),
                "platformBottom" to AABB(
                    0.0,
                    resolution.height - 5.units,
                    resolution.width,
                    5.0.units
                )
            ),
            Double.POSITIVE_INFINITY,
            1.0
        )
    }

    override fun start(stage: Stage) {
        // Make a Display
        val resolution = Context.RESOLUTION_IN_UNITS
        val window = JfxWindow()

        EventManager.registerJfxKeyEvents(window)

        this.colliders.add(boundingBox)

        for (i in 0..999) {
            val (x, y) = Point(
                ThreadLocalRandom.current().nextInt(10, (resolution.width - 10.units).toInt())
                    .toDouble(),
                ThreadLocalRandom.current().nextInt(10, (resolution.height - 10.units).toInt())
                    .toDouble()
            )
            this.colliders.add(Collider(Circle(x, y, 2.0.units), 5.units, 1.0))
        }

        this.physicsSimulator = PhysicsSimulator(144.0, colliders) { render(window) }

        runBlocking {
            physicsSimulator.physicsEngine.paused = true

            colliders.forEach {
                it.addForce(Direction.random().vector.scale(2.0.units), 0.1)
            }

            EventManager.getJfxKeyEventStream().addConsumer {
                if (it.code == KeyCode.SPACE && it.eventType == KeyEvent.KEY_PRESSED) {
                    physicsSimulator.physicsEngine.paused = !physicsSimulator.physicsEngine.paused
                }
            }
        }

        this.physicsSimulator.simulate()
        stage.scene = window
        stage.show()
    }

    private suspend fun render(window: JfxWindow) {
        window.canvas.clear()
        this.physicsSimulator.physicsEngine.render(window.canvas, 0.0, 0.0)
        this.colliders.forEach { c: Collider ->
            val canvasSettings = if (c.immovable) {
                Settings(color = Color(255.0, 0.0, 0.0, 1.0))
            } else {
                Settings(color = ballColour)
            }
            window.canvas.withSettings(canvasSettings) {
                c.render(window.canvas, 0.0, 0.0)
            }
        }
    }
}
