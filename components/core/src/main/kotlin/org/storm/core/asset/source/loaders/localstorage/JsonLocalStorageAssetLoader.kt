package org.storm.core.asset.source.loaders.localstorage

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.FileInputStream

/**
 * An AssetLoader that loads JSON from the local storage.
 */
class JsonLocalStorageAssetLoader : LocalStorageAssetLoader(extensions = setOf("json")) {

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }

    override fun <T> load(file: File, typeRef: TypeReference<T>): T? {
        return objectMapper.readValue(FileInputStream(file), typeRef)
    }

}
