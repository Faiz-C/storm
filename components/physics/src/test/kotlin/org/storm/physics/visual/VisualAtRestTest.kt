package org.storm.physics.visual

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.ui.Window
import org.storm.physics.context.UNIT_CONVERTOR
import org.storm.physics.entity.Entity
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.transforms.UnitConvertor

class VisualAtRestTest : Application() {

    private val unitConvertor: UnitConvertor = Context.UNIT_CONVERTOR
    private val platform: Entity = ImmovableEntity(
        AABB(
            unitConvertor.toUnits(1.0),
            unitConvertor.toUnits(Context.RESOLUTION.height - 20),
            unitConvertor.toUnits(Context.RESOLUTION.width - 2),
            unitConvertor.toUnits(10.0)
        )
    )
    private val repellingBall: Entity = SimpleEntity(
        AABB(
            unitConvertor.toUnits(75.0),
            unitConvertor.toUnits(400.0),
            unitConvertor.toUnits(20.0),
            unitConvertor.toUnits(20.0)
        ),
        3.0,
        10.0,
        0.7
    )
    private val repellingBall2: Entity = SimpleEntity(
        AABB(
            unitConvertor.toUnits(150.0),
            unitConvertor.toUnits(50.0),
            unitConvertor.toUnits(20.0),
            unitConvertor.toUnits(20.0)
        ),
        3.0,
        10.0,
        0.5
    )
    private val repellingBall3: Entity = SimpleEntity(
        AABB(
            unitConvertor.toUnits(220.0),
            unitConvertor.toUnits(200.0),
            unitConvertor.toUnits(20.0),
            unitConvertor.toUnits(20.0)
        ),
        3.0,
        10.0,
        1.0
    )
    private val repellingBall4: Entity = SimpleEntity(
        AABB(
            unitConvertor.toUnits(300.0),
            unitConvertor.toUnits(100.0),
            unitConvertor.toUnits(20.0),
            unitConvertor.toUnits(20.0)
        ),
        3.0,
        10.0,
        0.2
    )
    private lateinit var physicsSimulator: PhysicsSimulator

    override fun start(stage: Stage) {
        // Make a Display
        val window = Window()
        physicsSimulator = PhysicsSimulator(400.0) { render(window) }
        physicsSimulator.physicsEngine.entities =
            setOf(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4)
        repellingBall.addForce(Direction.SOUTH.vector.scale(unitConvertor.toUnits(10.0)))
        repellingBall.addForce(Direction.NORTH.vector.scale(unitConvertor.toUnits(30.0)), 2.0)
        repellingBall2.addForce(Direction.SOUTH.vector.scale(unitConvertor.toUnits(30.0)))
        repellingBall3.addForce(Direction.SOUTH.vector.scale(unitConvertor.toUnits(25.0)))
        repellingBall4.addForce(Direction.SOUTH.vector.scale(unitConvertor.toUnits(25.0)))

        physicsSimulator.physicsEngine.paused = true
        window.onKeyPressed = EventHandler { keyEvent: KeyEvent ->
            if (keyEvent.code == KeyCode.SPACE) {
                physicsSimulator.physicsEngine.paused = !physicsSimulator.physicsEngine.paused
            }
        }

        physicsSimulator.simulate()
        stage.scene = window
        stage.show()
    }

    private suspend fun render(window: Window) {
        window.clear()
        physicsSimulator.physicsEngine.render(window.graphicsContext, 0.0, 0.0)
        platform.render(window.graphicsContext, 0.0, 0.0)
        repellingBall.render(window.graphicsContext, 0.0, 0.0)
        repellingBall2.render(window.graphicsContext, 0.0, 0.0)
        repellingBall3.render(window.graphicsContext, 0.0, 0.0)
        repellingBall4.render(window.graphicsContext, 0.0, 0.0)
    }

}
