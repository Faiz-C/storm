package org.storm.storyboard

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.storm.core.input.Processor
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.core.asset.serialization.AssetResolver

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(AssetResolver::class)
interface StoryBoardState: Renderable, Updatable, Processor {
    val name: String
    val type: String
    val next: String?
    val neighbourStates: Set<String>

    fun isComplete(): Boolean
}
