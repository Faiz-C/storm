package org.storm.core.asset.source

import org.storm.core.asset.source.loaders.AssetLoader
import org.storm.core.asset.source.loaders.AssetSource

/**
 * An AssetSource describes a source where assets can be loaded from. It contains a context builder and a list of loaders
 * TODO: AssetSource -> Interface that abstracts loading from that source
 *       LocalStorageAssetSource -> Implementation that handles dealing with local storage access
 */
abstract class AssetSourceBase(
    val id: String,
    protected val loaders: List<AssetLoader>
): AssetSource
