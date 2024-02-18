package org.storm.core.asset.source

import org.storm.core.asset.source.context.AssetContextBuilder
import org.storm.core.asset.source.loaders.AssetLoader

data class AssetSource(
    val id: String,
    val contextBuilder: AssetContextBuilder,
    val loaders: List<AssetLoader>
)
