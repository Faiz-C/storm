package org.storm.core.asset.source.loaders

import com.fasterxml.jackson.core.type.TypeReference

interface AssetSource {
    val id: String
    val loaders: List<AssetLoader>

    fun <T> loadAsset(assetType: String, assetId: String, typeRef: TypeReference<T>): T
}
