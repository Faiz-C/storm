package org.storm.engine

import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import org.apache.commons.math3.util.FastMath
import org.storm.core.input.Translator
import org.storm.core.input.action.ActionManager
import org.storm.core.input.action.SimpleActionManager
import org.storm.core.ui.Resolution
import org.storm.core.ui.Window
import org.storm.core.utils.onInterval
import org.storm.engine.exception.StormEngineException
import org.storm.engine.request.Request
import org.storm.engine.request.RequestQueue
import org.storm.engine.state.State
import org.storm.physics.ImpulseResolutionPhysicsEngine
import org.storm.physics.PhysicsEngine
import org.storm.physics.transforms.UnitConvertor

/**
 * The StormEngine combines the multiple components of the Storm libraries to create a simple, straight forward and
 * efficient 2D game engine.
 */
class StormEngine(
  private val resolution: Resolution = Resolution.SD, // Default to 640 x 480
  private val unitConvertor: UnitConvertor = object : UnitConvertor {}, // Default to standard Unit Convertor (1 unit = 10 pixels)
  private val actionManager: ActionManager = SimpleActionManager(),
  val physicsEngine: PhysicsEngine = ImpulseResolutionPhysicsEngine(resolution, unitConvertor),
  renderFps: Int = 60,
  val logicFps: Int = renderFps,
  singleInputRequestMax: Int = 25
) {

  companion object {

    // The upper bound allowed for elapsed time is 1/4 a second, but we represent it in nanoseconds so multiply by
    // 1000000000 -> 1000000000/4 = 250000000
    private const val ELAPSED_TIME_UPPER_BOUND = 250000000L

    /**
     * @param nanoSeconds time (in nanoseconds) to convert to seconds
     * @return the given nanoseconds in seconds
     */
    private fun toSeconds(nanoSeconds: Long): Double {
      return nanoSeconds / 1000000000.0
    }
  }

  // Window the game will be run on
  val window: Window = Window(resolution)

  // Game Loop Vars
  private var accumulator: Long = 0L
  private var fixedTimeStepInterval: Long = 0L
  private var lastUpdateTime: Long = 0L

  // To allow for separate rendering and physics rates we use fps ratios and delays (calculated in the setter for render fps)
  private var renderFpsRatio: Int = 0
  private var renderDelay: Int = 0

  private var physicsFpsRatio: Int = 0
  private var physicsDelay: Int = 0

  private var gameLoop: Job? = null
  private val gameLoopScope: CoroutineScope = CoroutineScope(Dispatchers.JavaFx)

  // Stateful Vars
  private var currentState: State? = null
  private val states: MutableMap<String, State> = mutableMapOf()
  private val requestQueue: RequestQueue = RequestQueue(singleInputRequestMax)

  // Internal var is used to determine if the Engine has been started at least once and to avoid starting it multiple times
  private var running: Boolean = false

  // External var used to toggle rendering, input translation, and physics. Essentially the entire game loop.
  var paused: Boolean = false

  var fpsChangeAllow: Boolean = true

  var renderFps: Int = renderFps
    set(value) {
      if (!this.fpsChangeAllow) {
        throw StormEngineException("fps change not allowed at this time")
      }

      this.gameLoop?.cancel() // Stop the current loop if its running

      // Figure out which of the two is the greater fps, that will be the rate the loop will run at
      val higher = FastMath.max(value, this.logicFps)

      // We capture both ratios as we do not enforce that any one fps amount must be smaller than the other
      // This allows us to separate the physics and the rendering rates during the game loop
      this.renderFpsRatio = FastMath.ceil(higher.toDouble() / value).toInt()
      this.physicsFpsRatio = FastMath.ceil(higher.toDouble() / this.logicFps).toInt()

      // 1 / fps == fixed number of calls per second, but we want it in terms of nanoseconds as that is our tracking
      // metric, therefore we multiply by 1000000000 to go from seconds to nanoseconds -> 1000000000 / fps
      this.fixedTimeStepInterval = 1000000000L / this.logicFps

      // Restart the loop with the new interval rate
      this.gameLoop = this.gameLoopScope.onInterval(1000L / higher) {
        this.runGameLogic()
      }

      field = value
    }

  init {
    this.renderFps = renderFps
    this.physicsEngine.paused = true
  }

  /**
   * Adds the given State under the given unique id. This will also initialize the state
   *
   * @param stateId unique id to identify the state by
   * @param state   State to add
   */
  fun addState(stateId: String, state: State) {
    state.preload(requestQueue)
    this.states[stateId] = state
  }

  /**
   * Removes the State associated with the given id
   *
   * @param stateId unique id of the State to remove
   */
  fun removeState(stateId: String) {
    this.states.remove(stateId)
  }

  /**
   * Swaps the current active State to the one associated with the given id with or without resetting it.
   *
   * @param stateId unique id of the state to switch to
   * @param reset   true if resetting the state is wanted, false otherwise (default false)
   */
  fun swapState(stateId: String, reset: Boolean = false) {
    val newState = this.states[stateId] ?: throw StormEngineException("could not find state for id $stateId")

    if (newState === this.currentState) return

    // Stop the engine temporarily during the swap
    this.paused = true

    this.currentState?.unload(this.requestQueue)

    if (reset) newState.reset(this.requestQueue)

    this.currentState = newState

    this.physicsEngine.clearAllForces()

    this.physicsEngine.entities = this.currentState!!.entities

    this.currentState!!.load(requestQueue)

    // Resume the engine
    this.paused = false
  }

  /**
   * Registers the given translator to translate KeyEvents to String actions for the following events:
   * key pressed, key released
   *
   * @param inputTranslator Translator to use
   */
  fun addKeyTranslator(inputTranslator: Translator<KeyEvent, String>) {
    window.onKeyPressed = EventHandler { keyEvent -> actionManager.startUsing(inputTranslator.translate(keyEvent)) }
    window.onKeyReleased = EventHandler { keyEvent -> actionManager.stopUsing(inputTranslator.translate(keyEvent)) }
  }

  /**
   * Registers the given translator to translate MouseEvents to String actions for the following events:
   * mouse pressed, mouse released, mouse entered, mouse exited
   *
   * @param inputTranslator Translator to use
   */
  fun addMouseTranslator(inputTranslator: Translator<MouseEvent, String>) {
    window.onMousePressed = EventHandler { mouseEvent -> actionManager.startUsing(inputTranslator.translate(mouseEvent)) }
    window.onMouseReleased = EventHandler { mouseEvent -> actionManager.stopUsing(inputTranslator.translate(mouseEvent)) }
    window.onMouseEntered = EventHandler { mouseEvent -> actionManager.startUsing(inputTranslator.translate(mouseEvent)) }
    window.onMouseExited = EventHandler { mouseEvent -> actionManager.stopUsing(inputTranslator.translate(mouseEvent)) }
  }

  /**
   * Runs the StormEngine
   */
  fun run() {
    if (this.running) throw StormEngineException("StormEngine is already running")

    this.lastUpdateTime = System.nanoTime()
    this.running = true
  }

  /**
   * Internal run method which represents the game loop and actually runs the engine
   */
  private fun runGameLogic() {
    if (this.paused || !this.running || this.currentState == null) return

    val now = System.nanoTime()
    val elapsedFrameTime = now - this.lastUpdateTime

    // Don't let it creep up too much, upper bound it
    val adjustedElapsedFrameTime = if (elapsedFrameTime > ELAPSED_TIME_UPPER_BOUND) ELAPSED_TIME_UPPER_BOUND else elapsedFrameTime

    this.lastUpdateTime = now
    this.accumulator += adjustedElapsedFrameTime

    // First handle a set of requests
    this.requestQueue.next()?.let { requests: List<Request> ->
      requests.forEach { request -> request.execute(this) }
    }

    // Process Input
    this.currentState!!.process(this.actionManager.readonly, this.requestQueue)

    // Then allow the state to do any internal updating
    this.currentState!!.update(toSeconds(this.lastUpdateTime), toSeconds(elapsedFrameTime))

    if (++this.physicsDelay >= this.physicsFpsRatio) {
      // Apply Physics for as long as we have leeway through our accumulator
      while (this.accumulator >= this.fixedTimeStepInterval) {
        this.physicsEngine.update(toSeconds(this.lastUpdateTime), toSeconds(elapsedFrameTime))
        this.accumulator -= this.fixedTimeStepInterval
        this.lastUpdateTime += this.fixedTimeStepInterval
      }
      this.physicsDelay = 0
    }

    if (++this.renderDelay >= this.renderFpsRatio) {
      this.window.clear()
      this.currentState!!.render(this.window.graphicsContext, 0.0, 0.0)
      this.renderDelay = 0
    }
  }
}
