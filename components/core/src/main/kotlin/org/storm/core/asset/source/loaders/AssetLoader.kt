package org.storm.core.asset.source.loaders

import com.fasterxml.jackson.core.type.TypeReference

/**
 * An AssetLoader is responsible for loading assets from a specific source.
 */
interface AssetLoader {

    /**
     * @param assetId The id of the asset to check.
     * @param context The context to use when checking if the asset is supported.
     *
     * @return True if the asset is supported, false otherwise.
     */
    fun supports(assetId: String, context: Map<String, Any>): Boolean

    /**
     * @param assetId The id of the asset to load.
     * @param context The context to use when loading the asset.
     * @param typeRef The type reference to use when loading the asset, used for deserialization.
     *
     * @return The loaded asset
     */
    fun <T> load(assetId: String, context: Map<String, Any>, typeRef: TypeReference<T>): T

}
