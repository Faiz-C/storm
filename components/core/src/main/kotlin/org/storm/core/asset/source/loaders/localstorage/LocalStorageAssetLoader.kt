package org.storm.core.asset.source.loaders.localstorage

import org.storm.core.asset.source.loaders.AssetLoader
import org.storm.core.asset.source.context.LocalStorageAssetContextBuilder
import org.storm.core.exception.AssetException

abstract class LocalStorageAssetLoader(
    private val extensions: Set<String>
): AssetLoader {

    override fun supports(assetId: String, context: Map<String, Any>): Boolean {
        // Consider if not passing an extension should result in an exception
        val extension = context[LocalStorageAssetContextBuilder.EXT] ?: return false
        return extensions.contains(extension)
    }

    protected fun createFilePath(assetId: String, context: Map<String, Any>): String {
        val directory = context[LocalStorageAssetContextBuilder.DIR]
            ?: throw AssetException("No directory path provided in context. Failed to load asset with id $assetId")

        val ext = context[LocalStorageAssetContextBuilder.EXT]
            ?: throw AssetException("No file ext provided in context. Failed to load asset with id $assetId")

        return "$directory/$assetId.$ext"
    }
}
