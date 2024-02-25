package org.storm.physics.visual

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window
import org.storm.physics.entity.Entity
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import org.storm.physics.transforms.UnitConvertor

class MultiPartBoundaryTest : Application() {

    private val unitConvertor: UnitConvertor = object : UnitConvertor {}

    private val boundingBox = ImmovableEntity(
        mutableMapOf(
            "platformTop" to AABB(
                0.0,
                0.0,
                this.unitConvertor.toUnits(Resolution.SD.width),
                this.unitConvertor.toUnits(5.0)
            ),
            "platformRight" to AABB(
                0.0,
                0.0,
                this.unitConvertor.toUnits(5.0),
                this.unitConvertor.toUnits(Resolution.SD.height)
            ),
            "platformBottom" to AABB(
                this.unitConvertor.toUnits(Resolution.SD.width - 5),
                0.0,
                this.unitConvertor.toUnits(5.0),
                this.unitConvertor.toUnits(Resolution.SD.height)
            ),
            "platformLeft" to AABB(
                0.0,
                this.unitConvertor.toUnits(Resolution.SD.height - 5),
                this.unitConvertor.toUnits(Resolution.SD.width),
                this.unitConvertor.toUnits(5.0)
            )
        )
    )

    private val multiPartBoundaryEntity = SimpleEntity(
        mutableMapOf(
            "centerCircle" to Circle(
                Point(this.unitConvertor.toUnits(150.0), this.unitConvertor.toUnits(215.0)),
                unitConvertor.toUnits(18.0)
            ),
            "rectangleTop" to AABB(
                this.unitConvertor.toUnits(145.0),
                this.unitConvertor.toUnits(175.0),
                this.unitConvertor.toUnits(10.0),
                this.unitConvertor.toUnits(40.0)
            ),
            "rectangleRight" to AABB(
                this.unitConvertor.toUnits(150.0),
                this.unitConvertor.toUnits(210.0),
                this.unitConvertor.toUnits(40.0),
                this.unitConvertor.toUnits(10.0)
            ),
            "rectangleLeft" to AABB(
                this.unitConvertor.toUnits(110.0),
                this.unitConvertor.toUnits(210.0),
                this.unitConvertor.toUnits(40.0),
                this.unitConvertor.toUnits(10.0)
            ),
            "rectangleBottom" to AABB(
                this.unitConvertor.toUnits(145.0),
                this.unitConvertor.toUnits(215.0),
                this.unitConvertor.toUnits(10.0),
                this.unitConvertor.toUnits(40.0)
            )
        ),
        this.unitConvertor.toUnits(3.0),
        10.0,
        1.0
    )

    private val entities: MutableSet<Entity> = mutableSetOf()
    private lateinit var physicsSimulator: PhysicsSimulator

    override fun start(stage: Stage) {

        // Make a Display
        val window = Window(Resolution.SD)
        this.physicsSimulator = PhysicsSimulator(Resolution.SD, 144.0) { render(window) }
        this.entities.add(boundingBox)
        this.entities.add(multiPartBoundaryEntity)

        this.physicsSimulator.physicsEngine.entities = this.entities

        multiPartBoundaryEntity.addForce(Direction.NORTH_WEST.vector.scale(this.unitConvertor.toUnits(100.0)), 0.1)
        multiPartBoundaryEntity.addForce(Direction.NORTH.vector.scale(this.unitConvertor.toUnits(100.0)), 0.1)

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

    private suspend fun render(window: Window) {
        window.clear()
        val gc = window.graphicsContext
        window.graphicsContext.save()

        this.physicsSimulator.physicsEngine.render(gc, 0.0, 0.0)
        this.entities.forEach { e: Entity ->
            if (e is ImmovableEntity) {
                gc.fill = Color.RED
            } else {
                gc.fill = Color.BLUE
            }
            e.transform().render(gc, 0.0, 0.0)
        }
        window.graphicsContext.restore()
    }
}
