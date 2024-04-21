package org.storm.core.asset.source.loaders

import com.fasterxml.jackson.core.type.TypeReference

/**
 * An AssetLoader is responsible for loading assets from a specific source.
 */
interface AssetLoader {

    /**
     * @param assetType the type of the asset
     * @param assetId The id of the asset to load.
     * @param typeRef The type reference to use when loading the asset, used for deserialization.
     * @param context The context to use when loading the asset.
     *
     * @return The loaded asset, or null if not found or unable to be loaded
     */
    fun <T> load(assetType: String, assetId: String, context: Map<String, String>, typeRef: TypeReference<T>): T?

}
