package org.storm.storyboard

import javafx.scene.canvas.GraphicsContext
import org.storm.core.asset.AssetManager
import org.storm.core.input.Processor
import org.storm.core.input.action.ActionManager
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.exception.StoryBoardEngineException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

open class StoryBoardEngine(
    protected val assetManager: AssetManager = AssetManager(),
    protected val preloadNeighbourStates: Boolean = true
): Renderable, Updatable, Processor {

    protected val states: MutableMap<String, StoryBoardState> = ConcurrentHashMap()
    protected lateinit var currentState: StoryBoardState

    fun setStates(states: Map<String, StoryBoardState>) {
        this.states.clear()
        states.forEach { (k, v) ->
            this.states[k] = v
        }
    }

    fun setStartingState(stateId: String) {
        switchState(stateId)
    }

    open fun loadStatesFrom(stateId: String) {
        val queue = ConcurrentLinkedQueue<String>().also {
            it.add(stateId)
        }

        val visited = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val state = getState(queue.poll())

            states[state.name] = state

            visited.add(state.name)

            state.neighbourStates.filter {
                !visited.contains(it)
            }.forEach {
                queue.add(it)
            }
        }
    }

    override fun process(actionManager: ActionManager) {
        currentState.process(actionManager)
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        currentState.render(gc, x, y)
    }

    override fun update(time: Double, elapsedTime: Double) {
        val state = currentState
        state.update(time, elapsedTime)

        if (!state.isComplete() || state.next == null) return

        switchState(state.next!!)
    }

    protected open fun getState(stateId: String): StoryBoardState {
        return try {
            assetManager.getAsset(stateId, StoryBoardState::class.java)
        } catch (e: Exception) {
            throw StoryBoardEngineException("Failed to load state $stateId from from disk", e)
        }
    }

    private fun switchState(stateId: String) {
        val previousState = if (this::currentState.isInitialized) {
            currentState
        } else {
            null
        }

        currentState = states[stateId] ?: run {
            val state = getState(stateId)
            states[stateId] = state
            state
        }

        if (!preloadNeighbourStates || previousState == null) return

        val newNeighbours = currentState.neighbourStates - previousState.neighbourStates
        val oldNeighbours = previousState.neighbourStates - currentState.neighbourStates

        // unload any states that are not neighbours for our new state
        oldNeighbours.forEach {
            states.remove(it)
        }

        // Load all states which are now neighbours
        newNeighbours.forEach {
            states[it] = getState(it)
        }
    }
}
