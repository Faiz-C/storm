package org.storm.storyboard

import javafx.scene.canvas.GraphicsContext
import org.storm.core.asset.AssetManager
import org.storm.core.input.Processor
import org.storm.core.input.action.ActionManager
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.exception.StoryBoardEngineException
import java.util.concurrent.ConcurrentHashMap

open class StoryBoardEngine(
    protected val assetSourceId: String,
    protected val assetManager: AssetManager = AssetManager(),
    startState: StoryBoardState? = null,
): Renderable, Updatable, Processor {

    protected val states: MutableMap<String, StoryBoardState> = ConcurrentHashMap()
    protected var currentState: StoryBoardState? = startState

    open fun loadScene(sceneId: String) {
        val scene = assetManager.getAsset<List<StoryBoardState>>(sceneId, sourceId = assetSourceId)

        scene.forEach {
            states[it.id] = it
        }
    }

    open fun switchState(stateId: String) {
        val previousState = currentState

        val nextState = states[stateId]
            ?: throw StoryBoardEngineException("Failed to switch state from ${currentState?.id} to $stateId. No state found for id $stateId.")

        if (nextState.disabled) {
           throw StoryBoardEngineException("Failed to switch state from ${currentState?.id} to $stateId. State $stateId is disabled.")
        }

        currentState = nextState
    }

    fun setStates(states: Map<String, StoryBoardState>) {
        this.states.clear()
        states.forEach { (k, v) ->
            this.states[k] = v
        }
    }

    override fun process(actionManager: ActionManager) {
        currentState?.process(actionManager)
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        currentState?.render(gc, x, y)
    }

    override fun update(time: Double, elapsedTime: Double) {
        val state = currentState ?: return

        state.update(time, elapsedTime)

        if (!state.isComplete() || state.next == null) return

        switchState(state.next!!)
    }
}
