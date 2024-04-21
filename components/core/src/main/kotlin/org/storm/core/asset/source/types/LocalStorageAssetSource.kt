package org.storm.core.asset.source.types

import com.fasterxml.jackson.core.type.TypeReference
import org.storm.core.asset.source.loaders.AssetLoader
import org.storm.core.asset.source.loaders.AssetSource
import org.storm.core.asset.source.loaders.localstorage.ImageLocalStorageAssetLoader
import org.storm.core.asset.source.loaders.localstorage.JsonLocalStorageAssetLoader
import org.storm.core.asset.source.loaders.localstorage.YamlLocalStorageAssetLoader
import org.storm.core.exception.AssetException

class LocalStorageAssetSource(
    baseDir: String,
    override val id: String = "local-storage",
    override val loaders: List<AssetLoader> = listOf(
        YamlLocalStorageAssetLoader(),
        JsonLocalStorageAssetLoader(),
        ImageLocalStorageAssetLoader()
    )
): AssetSource {

    companion object {
        const val CONTEXT_DIR_FIELD = "dir"
    }

    private val context = mapOf(CONTEXT_DIR_FIELD to baseDir)

    override fun <T> loadAsset(assetType: String, assetId: String, typeRef: TypeReference<T>): T {
        this.loaders.forEach { loader ->
            loader.load(assetType, assetId, context, typeRef)?.let {
                return it
            }
        }

        throw AssetException("Failed to load asset of type $assetType, with id $assetId")

    }
}
