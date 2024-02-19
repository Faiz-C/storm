package org.storm.storyboard

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.storm.core.asset.Asset
import org.storm.core.input.Processor
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.core.asset.serialization.AssetResolver

/**
 * A State in a StoryBoard. This is a node in a directed graph that represents a specific point in time for a story.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(AssetResolver::class)
interface StoryBoardState: Renderable, Updatable, Processor {

    /**
     * The unique identifier for this state.
     */
    val id: String

    /**
     * The type of state, used to help serialize and deserialize the state using Asset loading.
     */
    val type: String
        get() {
            val assetAnnotation = this::class.java.getAnnotation(Asset::class.java)
            return "${assetAnnotation.type}-${assetAnnotation.impl}"
        }

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

}
