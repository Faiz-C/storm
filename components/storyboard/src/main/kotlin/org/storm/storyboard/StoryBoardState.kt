package org.storm.storyboard

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.storm.core.input.action.ActionStateProcessor
import org.storm.core.render.Renderable
import org.storm.core.serialization.PolymorphismResolver
import org.storm.core.update.Updatable

/**
 * A State in a StoryBoard. This is a node in a directed graph that represents a specific point in time for a story.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonTypeIdResolver(PolymorphismResolver::class)
@JsonIgnoreProperties("type")
interface StoryBoardState : Renderable, Updatable, ActionStateProcessor {

    /**
     * The unique identifier for this state.
     */
    val id: String

    /**
     * The list of neighbouring states. This is a directed graph, so the neighbours are the states that can be navigated to
     * from this state.
     */
    val neighbours: List<String>

    /**
     * Determines if the state is disabled or not. A disabled state cannot be navigated to.
     */
    var disabled: Boolean

    /**
     * Determines if the state is a terminal state or not. A terminal state is a state that has no neighbours.
     */
    val terminal get() = neighbours.isEmpty()

    /**
     * The next state to navigate to. This is the default implementation for a linear story. This should be overridden
     * in the case of multiple neighbours (i.e. a choice).
     */
    val next get() = neighbours.firstOrNull()

    /**
     * Determines if the state is complete or not. A complete state is a state that has finished its purpose and is ready
     * to move on to the next state.
     */
    fun isComplete(): Boolean

    /**
     * Resets the state to its initial state.
     */
    fun reset()

}
