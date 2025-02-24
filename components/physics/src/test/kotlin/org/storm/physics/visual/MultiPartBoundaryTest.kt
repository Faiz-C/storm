package org.storm.physics.visual

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION_IN_UNITS
import org.storm.core.extensions.units
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.canvas.Color
import org.storm.core.render.canvas.Settings
import org.storm.core.render.geometry.Point
import org.storm.core.ui.impl.JfxWindow
import org.storm.physics.entity.Entity
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle

class MultiPartBoundaryTest : Application() {

    private val resolution = Context.RESOLUTION_IN_UNITS

    private val boundingBox = ImmovableEntity(
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
        )
    )

    private val multiPartBoundaryEntity = SimpleEntity(
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
        3.0.units,
        10.0,
        1.0
    )

    private val entities: MutableSet<Entity> = mutableSetOf()
    private lateinit var physicsSimulator: PhysicsSimulator

    override fun start(stage: Stage) {

        // Make a Display
        val window = JfxWindow()
        this.physicsSimulator = PhysicsSimulator(144.0) { render(window) }
        this.entities.add(boundingBox)
        this.entities.add(multiPartBoundaryEntity)

        this.physicsSimulator.physicsEngine.entities = this.entities

        multiPartBoundaryEntity.addForce(Direction.NORTH_WEST.vector.scale(100.0.units), 0.1)
        multiPartBoundaryEntity.addForce(Direction.NORTH.vector.scale(100.0.units), 0.1)

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
                Settings(color = Color(0.0, 255.0, 0.0, 1.0))
            }

            window.canvas.withSettings(canvasSettings) {
                e.render(it, 0.0, 0.0)
            }
        }
    }
}
