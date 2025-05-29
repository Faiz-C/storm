package org.storm.physics.visual

import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.event.EventManager
import org.storm.core.extensions.units
import org.storm.core.graphics.canvas.Color
import org.storm.core.graphics.canvas.Settings
import org.storm.core.graphics.geometry.Point
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.extensions.registerJfxKeyEvents
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.collision.Collider
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle

class MultiPartBoundaryTest : Application() {

    private val resolution = Context.RESOLUTION_IN_UNITS

    private val boundingBox = Collider(
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
                resolution.height
            ),
            "platformLeft" to AABB(
                0.0,
                resolution.height - 5.units,
                resolution.width,
                5.0.units
            )
        ),
        Double.POSITIVE_INFINITY,
        1.0
    )

    private val multiPartBoundaryEntity = Collider(
        mutableMapOf(
            "centerCircle" to Circle(
                Point(150.0.units, 215.0.units),
                18.0.units
            ),
            "rectangleTop" to AABB(
                145.0.units,
                175.0.units,
                10.0.units,
                40.0.units
            ),
            "rectangleRight" to AABB(
                150.0.units,
                210.0.units,
                40.0.units,
                10.0.units
            ),
            "rectangleLeft" to AABB(
                110.0.units,
                210.0.units,
                40.0.units,
                10.0.units
            ),
            "rectangleBottom" to AABB(
                145.0.units,
                215.0.units,
                10.0.units,
                40.0.units
            )
        ),
        100.0.units,
        1.0
    )

    private val colliders: MutableSet<Collider> = mutableSetOf()
    private lateinit var physicsSimulator: PhysicsSimulator

    override fun start(stage: Stage) {

        // Make a Display
        val window = JfxWindow()

        EventManager.registerJfxKeyEvents(window)

        this.physicsSimulator = PhysicsSimulator(144.0) { render(window) }
        this.colliders.add(boundingBox)
        this.colliders.add(multiPartBoundaryEntity)

        runBlocking {
            physicsSimulator.physicsEngine.paused = true

            physicsSimulator.physicsEngine.applyForce(Direction.NORTH_WEST.vector.scale(100.0.units), multiPartBoundaryEntity, 0.1)
            physicsSimulator.physicsEngine.applyForce(Direction.NORTH.vector.scale(100.0.units), multiPartBoundaryEntity, 0.1)

            physicsSimulator.physicsEngine.setColliders(colliders)

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
                Settings(color = Color(0.0, 255.0, 0.0, 1.0))
            }

            window.canvas.withSettings(canvasSettings) {
                c.render(it, 0.0, 0.0)
            }
        }
    }
}
