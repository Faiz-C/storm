package org.storm.core.asset.loaders

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.FileInputStream

class JsonAssetLoader(override val extensions: List<String> = listOf("json")) : AssetLoader {
    private val objectMapper = jacksonObjectMapper()

    override fun <T> load(path: String, clazz: Class<T>): T {
        return objectMapper.readValue(FileInputStream(path), clazz)
    }
}
