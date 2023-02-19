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
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ParticleTest : Application() {

  private val unitConvertor: UnitConvertor = object : UnitConvertor {}

  private val platformTop: Entity = ImmovableEntity(
    AABB(
      0.0,
      0.0,
      this.unitConvertor.toUnits(Resolution.SD.width),
      this.unitConvertor.toUnits(5.0)
    )
  )
  private val platformLeft: Entity = ImmovableEntity(
    AABB(
      0.0,
      0.0,
      this.unitConvertor.toUnits(5.0),
      this.unitConvertor.toUnits(Resolution.SD.height)
    )
  )
  private val platformRight: Entity = ImmovableEntity(
    AABB(
      this.unitConvertor.toUnits(Resolution.SD.width - 5),
      0.0,
      this.unitConvertor.toUnits(5.0),
      this.unitConvertor.toUnits(Resolution.SD.height)
    )
  )
  private val platformBottom: Entity = ImmovableEntity(
    AABB(
      0.0,
      this.unitConvertor.toUnits(Resolution.SD.height - 5),
      this.unitConvertor.toUnits(Resolution.SD.width),
      this.unitConvertor.toUnits(5.0)
    )
  )
  private val entities: MutableSet<Entity> = mutableSetOf()
  private lateinit var simulator: Simulator
  private val ballColour = Color.rgb(
    ThreadLocalRandom.current().nextInt(255),
    ThreadLocalRandom.current().nextInt(255),
    ThreadLocalRandom.current().nextInt(255)
  )

  override fun start(stage: Stage) {

    // Make a Display
    val window = Window(Resolution.SD)
    this.simulator = Simulator(Resolution.SD, 144.0) { render(window) }
    this.entities.add(platformTop)
    this.entities.add(platformLeft)
    this.entities.add(platformRight)
    this.entities.add(platformBottom)

    for (i in 0..999) {
      val (x, y) = Point(
        ThreadLocalRandom.current().nextInt(10, this.unitConvertor.toUnits(Resolution.SD.width - 10).toInt())
          .toDouble(),
        ThreadLocalRandom.current().nextInt(10, this.unitConvertor.toUnits(Resolution.SD.height - 10).toInt())
          .toDouble()
      )
      this.entities.add(SimpleEntity(Circle(x, y, this.unitConvertor.toUnits(2.0)), 3.0, 0.5, 1.0))
    }

    this.simulator.physicsEngine.entities = this.entities

    this.entities.forEach {
      it.addForce(Direction.random().vector.scale(this.unitConvertor.toUnits(2.0)), 0.1)
    }

    this.simulator.physicsEngine.paused = true

    window.onKeyPressed = EventHandler { keyEvent: KeyEvent ->
      if (keyEvent.code == KeyCode.SPACE) {
        this.simulator.physicsEngine.paused = !this.simulator.physicsEngine.paused
      }
    }

    this.simulator.simulate()
    stage.scene = window
    stage.show()
  }

  private fun render(window: Window) {
    window.clear()
    val gc = window.graphicsContext
    window.graphicsContext.save()
    this.simulator.physicsEngine.render(gc, 0.0, 0.0)
    this.entities.forEach(Consumer { e: Entity ->
      if (e is ImmovableEntity) {
        gc.fill = Color.RED
      } else {
        gc.fill = ballColour
      }
      e.transform().render(gc, 0.0, 0.0)
    })
    window.graphicsContext.restore()
  }
}