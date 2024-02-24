package org.storm.core.asset.source

import org.storm.core.asset.source.context.AssetContextBuilder
import org.storm.core.asset.source.loaders.AssetLoader

/**
 * An AssetSource describes a source where assets can be loaded from. It contains a context builder and a list of loaders
 */
data class AssetSource(
    val id: String,
    val contextBuilder: AssetContextBuilder,
    val loaders: List<AssetLoader>
)
