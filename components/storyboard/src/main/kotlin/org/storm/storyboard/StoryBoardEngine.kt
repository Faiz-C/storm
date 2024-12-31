package org.storm.storyboard

import org.storm.core.asset.AssetManager
import org.storm.core.input.action.ActionStateProcessor
import org.storm.core.input.action.ActionState
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.exception.StoryBoardEngineException
import java.util.concurrent.ConcurrentHashMap

/**
 * A StoryBoardEngine is a state machine which can be used to navigate between different storyboard states. States are
 * loaded as assets from a single source.
 */
open class StoryBoardEngine(
    protected val assetSourceId: String,
    protected val assetManager: AssetManager,
    protected val assetType: String = "scene"
) : Renderable, Updatable, ActionStateProcessor {

    protected val states: MutableMap<String, StoryBoardState> = ConcurrentHashMap()
    protected var currentState: StoryBoardState? = null

    /**
     * Load a "Scene", a collection of states, and ready the states for use.
     *
     * @param sceneId the asset id of the scene to load
     */
    open fun loadScene(sceneId: String) {
        val scene = assetManager.getAsset<List<StoryBoardState>>(assetType, sceneId, assetSourceId = assetSourceId)
        setStates(scene.associateBy { it.id })
    }

    /**
     * Switch the current state of the StoryBoardEngine to the state with the given id.
     *
     * @param stateId the asset id of the state to switch to
     */
    open fun switchState(stateId: String) {
        val nextState = states[stateId]
            ?: throw StoryBoardEngineException("Failed to switch state from ${currentState?.id} to $stateId. No state found for id $stateId.")

        if (nextState.disabled) {
            throw StoryBoardEngineException("Failed to switch state from ${currentState?.id} to $stateId. State $stateId is disabled.")
        }

        currentState = nextState.also {
            it.reset()
        }
    }

    /**
     * Set the states of the StoryBoardEngine to the given states.
     *
     * @param states a map of state ids to states
     */
    fun setStates(states: Map<String, StoryBoardState>) {
        this.states.clear()
        states.forEach { (k, v) ->
            this.states[k] = v
        }
    }

    /**
     * Clear all states from the StoryBoardEngine.
     */
    fun clearStates() {
        states.clear()
    }

    override suspend fun process(actionState: ActionState) {
        currentState?.process(actionState)
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        currentState?.render(canvas, x, y)
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        val state = currentState ?: return

        state.update(time, elapsedTime)

        if (!state.isComplete() || state.terminal || state.next == null) return

        switchState(state.next!!)
    }
}
