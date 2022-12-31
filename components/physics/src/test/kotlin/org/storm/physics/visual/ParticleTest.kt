package org.storm.physics.visual

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.storm.core.ui.Resolutions
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
      unitConvertor.toUnits(Resolutions.SD.width),
      unitConvertor.toUnits(5.0)
    )
  )
  private val platformLeft: Entity = ImmovableEntity(
    AABB(
      0.0,
      0.0,
      unitConvertor.toUnits(5.0),
      unitConvertor.toUnits(Resolutions.SD.height)
    )
  )
  private val platformRight: Entity = ImmovableEntity(
    AABB(
      unitConvertor.toUnits(Resolutions.SD.width - 5),
      0.0,
      unitConvertor.toUnits(5.0),
      unitConvertor.toUnits(Resolutions.SD.height)
    )
  )
  private val platformBottom: Entity = ImmovableEntity(
    AABB(
      0.0,
      unitConvertor.toUnits(Resolutions.SD.height - 5),
      unitConvertor.toUnits(Resolutions.SD.width),
      unitConvertor.toUnits(5.0)
    )
  )
  private val entities: MutableList<Entity> = ArrayList()
  private lateinit var simulator: Simulator
  private val ballColour = Color.rgb(
    ThreadLocalRandom.current().nextInt(255),
    ThreadLocalRandom.current().nextInt(255),
    ThreadLocalRandom.current().nextInt(255)
  )

  override fun start(stage: Stage) {

    // Make a Display
    val window = Window(Resolutions.SD)
    simulator = Simulator(Resolutions.SD, 144.0) { render(window) }
    entities.add(platformTop)
    entities.add(platformLeft)
    entities.add(platformRight)
    entities.add(platformBottom)

    for (i in 0..999) {
      val (x, y) = Point(
        ThreadLocalRandom.current().nextInt(10, unitConvertor.toUnits(Resolutions.SD.width - 10).toInt())
          .toDouble(),
        ThreadLocalRandom.current().nextInt(10, unitConvertor.toUnits(Resolutions.SD.height - 10).toInt())
          .toDouble()
      )
      entities.add(SimpleEntity(Circle(x, y, unitConvertor.toUnits(2.0)), 3.0, 0.5, 1.0))
    }

    simulator.physicsEngine.entities = entities
    entities.forEach {
      it.addForce(Direction.random().vector.scale(unitConvertor.toUnits(2.0)), 0.1)
    }

    simulator.physicsEngine.paused = true

    window.onKeyPressed = EventHandler { keyEvent: KeyEvent ->
      if (keyEvent.code == KeyCode.SPACE) {
        simulator.physicsEngine.paused = !simulator.physicsEngine.paused
      }
    }

    simulator.simulate()
    stage.scene = window
    stage.show()
  }

  private fun render(window: Window) {
    window.clear()
    val gc = window.graphicsContext
    window.graphicsContext.save()
    simulator.physicsEngine.render(gc, 0.0, 0.0)
    entities.forEach(Consumer { e: Entity ->
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
