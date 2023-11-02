package org.storm.storyboard

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.storm.storyboard.dialogue.Script
import java.nio.file.Paths

class StoryBoardEngineTest {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val resourceDir = Paths.get("components", "storyboard", "src", "test", "resources", "states")
            val engine = StoryBoardEngine(resourceDir.toString())

            //engine.loadStates("concert")

            val yamlMapper = YAMLMapper()
                .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModules(
                    KotlinModule.Builder()
                        .withReflectionCacheSize(512)
                        .configure(KotlinFeature.NullToEmptyCollection, false)
                        .configure(KotlinFeature.NullToEmptyMap, false)
                        .configure(KotlinFeature.NullIsSameAsDefault, false)
                        .configure(KotlinFeature.SingletonSupport, true)
                        .configure(KotlinFeature.StrictNullChecks, false)
                        .build()
                ).findAndRegisterModules()!!

            data class Thing(
                private val a: String?,
                private val b: String?
            )

            data class Thing2(
                private val thing: List<Thing>
            )

            val script = yamlMapper.readValue<Thing2>(this.javaClass.classLoader.getResource("example.yml"))

            println(script)
        }
    }
}
