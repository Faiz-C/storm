package org.storm.storyboard

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLParser
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.scene.canvas.GraphicsContext
import org.apache.commons.lang3.ClassUtils
import org.storm.core.asset.Asset
import org.storm.core.asset.AssetManager
import org.storm.core.input.Processor
import org.storm.core.input.action.ActionManager
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
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
                .configure(KotlinFeature.NullIsSameAsDefault, false)
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

    open fun loadStates(stateId: String) {
        val queue = ConcurrentLinkedQueue<String>().also {
            it.add(stateId)
        }

        val visited = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val state = getState(queue.poll())

            states[state.name] = state

            state.neighbourStates.filter {
                !visited.contains(it)
            }.forEach {
                queue.add(it)
            }

            visited.add(state.name)
        }
    }

    override fun process(actionManager: ActionManager) {
        currentState.process(actionManager)
    }

    override fun render(gc: GraphicsContext, x: Double, y: Double) {
        currentState.render(gc, x, y)
    }

    override fun update(time: Double, elapsedTime: Double) {
        currentState.update(time, elapsedTime)

        if (!currentState.isComplete() || currentState.terminal) return

        switchState(currentState.next)
    }

    protected open fun getState(stateId: String): StoryBoardState {
        return if (useJson) {
            objectMapper.readValue(FileInputStream("$baseDir/$stateId.json"))
        } else {
            yamlMapper.readValue(FileInputStream("$baseDir/$stateId.yml"))
        }
    }

    private fun switchState(stateId: String) {
        val previousState = currentState
        currentState = states[stateId] ?: run {
            val state = getState(stateId)
            states[stateId] = state
            state
        }

        if (!preloadNeighbourStates) return

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
