package org.storm.core.asset.source.loaders.localstorage

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.storm.core.asset.Asset
import org.storm.core.asset.source.types.LocalStorageAssetSource
import org.storm.core.exception.AssetException
import java.io.File
import java.io.FileInputStream

/**
 * An AssetLoader that loads YAML from the local storage.
 */
class YamlLocalStorageAssetLoader : LocalStorageAssetLoader(extensions = setOf("yaml", "yml")) {

    companion object {
        private val yamlMapper = YAMLMapper()
            .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModules(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, true)
                    .configure(KotlinFeature.SingletonSupport, true)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            ).findAndRegisterModules()!!
    }

    override fun <T> load(file: File, typeRef: TypeReference<T>): T? {
        return yamlMapper.readValue(FileInputStream(file), typeRef)
    }

}
