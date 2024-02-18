package org.storm.core.asset.source.loaders.localstorage

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.storm.core.asset.source.context.LocalStorageAssetContextBuilder
import org.storm.core.exception.AssetException
import java.io.FileInputStream

class JsonLocalStorageAssetLoader : LocalStorageAssetLoader(extensions = setOf("json")) {

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }

    override fun <T> load(assetId: String, context: Map<String, Any>, typeRef: TypeReference<T>): T {
        return objectMapper.readValue(FileInputStream(createFilePath(assetId, context)), typeRef)
    }
}
