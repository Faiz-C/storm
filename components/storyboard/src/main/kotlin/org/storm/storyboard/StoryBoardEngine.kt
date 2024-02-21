package org.storm.storyboard

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.scene.canvas.GraphicsContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import org.storm.core.asset.AssetManager
import org.storm.core.input.ActionStateProcessor
import org.storm.core.input.ActionState
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.exception.StoryBoardEngineException
import java.util.concurrent.ConcurrentHashMap

open class StoryBoardEngine(
    protected val assetSourceId: String,
    protected val assetManager: AssetManager = AssetManager(),
): Renderable, Updatable, ActionStateProcessor {

    protected val states: MutableMap<String, StoryBoardState> = ConcurrentHashMap()
    protected var currentState: StoryBoardState? = null

    open fun loadScene(sceneId: String) {
        val scene = assetManager.getAsset<List<StoryBoardState>>(sceneId, sourceId = assetSourceId)
        setStates(scene.associateBy { it.id })
    }

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

    fun setStates(states: Map<String, StoryBoardState>) {
        this.states.clear()
        states.forEach { (k, v) ->
            this.states[k] = v
        }
    }

    fun clearStates() {
        states.clear()
    }

    override fun process(actionState: ActionState) {
        currentState?.process(actionState)
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
