package org.storm.core.asset.source.loaders

import com.fasterxml.jackson.core.type.TypeReference

interface AssetLoader {
    fun supports(assetId: String, context: Map<String, Any>): Boolean
    fun <T> load(assetId: String, context: Map<String, Any>, typeRef: TypeReference<T>): T
}
