package org.storm.core.asset.loaders

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.FileInputStream

class YamlAssetLoader(override val extensions: List<String> = listOf("yml", "yaml")) : AssetLoader {

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


    override fun load(path: String, clazz: Class<*>): Any {
        return yamlMapper.readValue(FileInputStream(path), clazz)
    }
}
