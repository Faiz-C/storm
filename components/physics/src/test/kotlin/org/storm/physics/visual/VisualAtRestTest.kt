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
import org.storm.impl.jfx.extensions.getJfxKeyEventStream
import org.storm.impl.jfx.graphics.JfxWindow
import org.storm.physics.collision.CollisionObject
import org.storm.physics.entity.ImmovableCollisionObject
import org.storm.physics.entity.SimpleCollisionObject
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.shapes.AABB

class VisualAtRestTest : Application() {

    private val resolution = Context.RESOLUTION_IN_UNITS

    private val platform: CollisionObject = ImmovableCollisionObject(
        AABB(
            1.units,
            resolution.height - 20.units,
            resolution.width - 2.units,
            10.units
        )
    )
    private val repellingBall: CollisionObject = SimpleCollisionObject(
        AABB(
            75.0.units,
            400.0.units,
            20.0.units,
            20.0.units
        ),
        3.0,
        10.0,
        0.7
    )
    private val repellingBall2: CollisionObject = SimpleCollisionObject(
        AABB(
            150.0.units,
            50.0.units,
            20.0.units,
            20.0.units
        ),
        3.0,
        10.0,
        0.5
    )
    private val repellingBall3: CollisionObject = SimpleCollisionObject(
        AABB(
            220.0.units,
            200.0.units,
            20.0.units,
            20.0.units
        ),
        3.0,
        10.0,
        1.0
    )
    private val repellingBall4: CollisionObject = SimpleCollisionObject(
        AABB(
            300.0.units,
            100.0.units,
            20.0.units,
            20.0.units
        ),
        3.0,
        10.0,
        0.2
    )
    private lateinit var physicsSimulator: PhysicsSimulator

    override fun start(stage: Stage) {
        // Make a Display
        val window = JfxWindow()
        physicsSimulator = PhysicsSimulator(400.0) { render(window) }
        physicsSimulator.physicsEngine.entities =
            setOf(platform, repellingBall, repellingBall2, repellingBall3, repellingBall4)
        repellingBall.addForce(Direction.SOUTH.vector.scale(10.0.units))
        repellingBall.addForce(Direction.NORTH.vector.scale(30.0.units), 2.0)
        repellingBall2.addForce(Direction.SOUTH.vector.scale(30.0.units))
        repellingBall3.addForce(Direction.SOUTH.vector.scale(25.0.units))
        repellingBall4.addForce(Direction.SOUTH.vector.scale(25.0.units))

        physicsSimulator.physicsEngine.paused = true

        runBlocking {
            EventManager.getJfxKeyEventStream().addConsumer {
                if (it.code == KeyCode.SPACE && it.eventType == KeyEvent.KEY_PRESSED) {
                    physicsSimulator.physicsEngine.paused = !physicsSimulator.physicsEngine.paused
                }
            }
        }

        physicsSimulator.simulate()
        stage.scene = window
        stage.show()
    }

    private suspend fun render(window: JfxWindow) {
        window.canvas.clear()
        physicsSimulator.physicsEngine.render(window.canvas, 0.0, 0.0)
        platform.render(window.canvas, 0.0, 0.0)
        repellingBall.render(window.canvas, 0.0, 0.0)
        repellingBall2.render(window.canvas, 0.0, 0.0)
        repellingBall3.render(window.canvas, 0.0, 0.0)
        repellingBall4.render(window.canvas, 0.0, 0.0)
    }

}
