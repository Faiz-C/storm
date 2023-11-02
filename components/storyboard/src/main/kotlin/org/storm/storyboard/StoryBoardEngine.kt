package org.storm.storyboard

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.scene.canvas.GraphicsContext
import org.storm.core.input.Processor
import org.storm.core.input.action.ActionManager
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.storyboard.exception.StoryBoardEngineException
import java.io.FileInputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

open class StoryBoardEngine(
    private val baseDir: String,
    private val preloadNeighbourStates: Boolean = true,
    private val useJson: Boolean = false,
): Renderable, Updatable, Processor {

    protected val states: MutableMap<String, StoryBoardState> = ConcurrentHashMap()
    protected lateinit var currentState: StoryBoardState

    protected val objectMapper = jacksonObjectMapper()
    protected val yamlMapper = YAMLMapper()
        .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModules(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, true)
                .configure(KotlinFeature.SingletonSupport, true)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        ).findAndRegisterModules()!!

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
            if (useJson) {
                objectMapper.readValue<StoryBoardState>(FileInputStream("$baseDir/$stateId.json"))
            } else {
                yamlMapper.readValue<StoryBoardState>(FileInputStream("$baseDir/$stateId.yml"))
            }
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
