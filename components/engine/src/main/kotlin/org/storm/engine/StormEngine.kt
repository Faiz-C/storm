package org.storm.engine

import kotlinx.coroutines.*
import org.apache.commons.math3.util.FastMath
import org.storm.core.context.Context
import org.storm.core.context.loadMappers
import org.storm.core.extensions.scheduleOnInterval
import org.storm.core.graphics.Window
import org.storm.core.input.action.ActionManager
import org.storm.core.sound.SoundManager
import org.storm.core.utils.TimeUtils.toSeconds
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.exception.StormEngineException
import org.storm.engine.request.Request
import org.storm.engine.state.GameState
import org.storm.physics.PhysicsEngine

/**
 * The StormEngine combines the multiple components of the Storm libraries to create a simple, straight forward and
 * efficient 2D game engine.
 */
class StormEngine(
    renderFps: Int = 60,
    val physicsFps: Int = renderFps,
    val window: Window,
    private val physicsEngine: PhysicsEngine,
    private val actionManager: ActionManager,
    private val soundManager: SoundManager,
    private val renderingDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    companion object {
        // The upper bound allowed for elapsed time is 1/4 a second, but we represent it in nanoseconds so multiply by
        // 1000000000 -> 1000000000/4 = 250000000
        private const val ELAPSED_TIME_UPPER_BOUND: Long = 250000000L
    }

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
    private val engineCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(1))

    // Stateful Vars
    private var currentState: GameState? = null
    private val states: MutableMap<String, GameState> = mutableMapOf()

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
            val higher = FastMath.max(value, this.physicsFps)

            // We capture both ratios as we do not enforce that any one fps amount must be smaller than the other
            // This allows us to separate the physics and the rendering rates during the game loop
            this.renderFpsRatio = FastMath.ceil(higher.toDouble() / value).toInt()
            this.physicsFpsRatio = FastMath.ceil(higher.toDouble() / this.physicsFps).toInt()

            // 1 / fps == fixed number of calls per second, but we want it in terms of nanoseconds as that is our tracking
            // metric, therefore we multiply by 1000000000 to go from seconds to nanoseconds -> 1000000000 / fps
            this.fixedTimeStepInterval = 1000000000L / this.physicsFps

            // Restart the loop with the new interval rate
            this.gameLoop = this.engineCoroutineScope.scheduleOnInterval(1000L / higher) {
                this.runGameLogic()
            }

            field = value
        }

    init {
        this.renderFps = renderFps
        this.physicsEngine.paused = true

        Context.loadMappers()
    }

    /**
     * Adds the given State under the given unique id. This will also initialize the state
     *
     * @param stateId unique id to identify the state by
     * @param state   State to add
     */
    suspend fun registerState(stateId: String, state: GameState) {
        state.onRegister(this.physicsEngine, this.soundManager)
        this.states[stateId] = state
    }

    /**
     * Removes the State associated with the given id
     *
     * @param stateId unique id of the State to remove
     */
    suspend fun unregisterState(stateId: String) {
        this.states[stateId]?.onUnregister(this.physicsEngine, this.soundManager)
        this.states.remove(stateId)
    }

    /**
     * Swaps the current active State to the one associated with the given id with or without resetting it.
     *
     * @param stateId unique id of the state to switch to
     */
    suspend fun swapState(stateId: String) {
        val newState = this.states[stateId] ?: throw StormEngineException("could not find state for id $stateId")

        if (newState === this.currentState) return

        // Stop the engine temporarily during the swap
        this.paused = true

        this.currentState?.onSwapOff(this.physicsEngine, this.soundManager)

        this.currentState = newState

        this.physicsEngine.setColliders(this.currentState!!.colliders)

        this.currentState!!.onSwapOn(this.physicsEngine, this.soundManager)

        // Resume the engine
        this.paused = false
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
    private suspend fun runGameLogic() {
        if (this.paused || !this.running || this.currentState == null) return

        val now = System.nanoTime()
        val elapsedFrameTime = now - this.lastUpdateTime

        // Don't let it creep up too much, upper bound it
        val adjustedElapsedFrameTime = if (elapsedFrameTime > ELAPSED_TIME_UPPER_BOUND) {
            ELAPSED_TIME_UPPER_BOUND
        } else {
            elapsedFrameTime
        }

        this.lastUpdateTime = now
        this.accumulator += adjustedElapsedFrameTime

        // First handle the next set of requests
        Context.REQUEST_QUEUE.next()?.let { requests: List<Request> ->
            requests.forEach { request -> request.execute(this, this.physicsEngine, this.soundManager) }
        }

        // Run all scheduled context updates
        Context.runScheduledUpdates()

        // Capture the current state of the game here so the next steps of the game loop doesn't
        // get disrupted by potential changes to the current state
        val frameState = this.currentState!!

        // Process Input
        this.actionManager.updateActiveFrames()
        frameState.process(this.actionManager.getStateSnapshot())

        // Then allow the state to do any internal updating
        frameState.update(toSeconds(this.lastUpdateTime), toSeconds(elapsedFrameTime))

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
            withContext(renderingDispatcher) {
                this@StormEngine.window.canvas.clear()
                this@StormEngine.currentState!!.render(this@StormEngine.window.canvas, 0.0, 0.0)
                this@StormEngine.renderDelay = 0
            }
        }
    }
}
