package org.storm.physics.visual

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.context.setResolution
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window
import org.storm.physics.context.UNIT_CONVERTOR
import org.storm.physics.entity.Entity
import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.entity.SimpleEntity
import org.storm.physics.enums.Direction
import org.storm.physics.math.geometry.Point
import org.storm.physics.math.geometry.shapes.AABB
import org.storm.physics.math.geometry.shapes.Circle
import java.util.concurrent.ThreadLocalRandom

class ParticleTest : Application() {

    private val boundingBox: ImmovableEntity
    private val entities: MutableSet<Entity> = mutableSetOf()

    private lateinit var physicsSimulator: PhysicsSimulator
    private val ballColour = Color.rgb(
        ThreadLocalRandom.current().nextInt(255),
        ThreadLocalRandom.current().nextInt(255),
        ThreadLocalRandom.current().nextInt(255)
    )

    init {
        Context.setResolution(Resolution.HD)
        val resolution = Context.RESOLUTION
        boundingBox = ImmovableEntity(
            mutableMapOf(
                "platformTop" to AABB(
                    0.0,
                    0.0,
                    Context.UNIT_CONVERTOR.toUnits(resolution.width),
                    Context.UNIT_CONVERTOR.toUnits(5.0)
                ),
                "platformRight" to AABB(
                    0.0,
                    0.0,
                    Context.UNIT_CONVERTOR.toUnits(5.0),
                    Context.UNIT_CONVERTOR.toUnits(resolution.height)
                ),
                "platformBottom" to AABB(
                    Context.UNIT_CONVERTOR.toUnits(resolution.width - 5),
                    0.0,
                    Context.UNIT_CONVERTOR.toUnits(5.0),
                    Context.UNIT_CONVERTOR.toUnits(resolution.height)
                ),
                "platformLeft" to AABB(
                    0.0,
                    Context.UNIT_CONVERTOR.toUnits(resolution.height - 5),
                    Context.UNIT_CONVERTOR.toUnits(resolution.width),
                    Context.UNIT_CONVERTOR.toUnits(5.0)
                )
            )
        )
    }

    override fun start(stage: Stage) {
        // Make a Display
        val resolution = Context.RESOLUTION
        val window = Window()
        this.physicsSimulator = PhysicsSimulator(144.0) { render(window) }
        this.entities.add(boundingBox)

        for (i in 0..999) {
            val (x, y) = Point(
                ThreadLocalRandom.current().nextInt(10, Context.UNIT_CONVERTOR.toUnits(resolution.width - 10).toInt())
                    .toDouble(),
                ThreadLocalRandom.current().nextInt(10, Context.UNIT_CONVERTOR.toUnits(resolution.height - 10).toInt())
                    .toDouble()
            )
            this.entities.add(SimpleEntity(Circle(x, y, Context.UNIT_CONVERTOR.toUnits(2.0)), 3.0, 0.5, 1.0))
        }

        this.physicsSimulator.physicsEngine.entities = this.entities

        this.entities.forEach {
            it.addForce(Direction.random().vector.scale(Context.UNIT_CONVERTOR.toUnits(2.0)), 0.1)
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

    private suspend fun render(window: Window) {
        window.clear()
        val gc = window.graphicsContext
        window.graphicsContext.save()
        this.physicsSimulator.physicsEngine.render(gc, 0.0, 0.0)
        this.entities.forEach { e: Entity ->
            if (e is ImmovableEntity) {
                gc.fill = Color.RED
            } else {
                gc.fill = ballColour
            }
            e.render(gc, 0.0, 0.0)
        }
        window.graphicsContext.restore()
    }
}
