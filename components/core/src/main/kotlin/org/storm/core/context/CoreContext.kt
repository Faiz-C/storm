package org.storm.core.context

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.storm.core.extensions.units
import org.storm.core.graphics.UnitConvertor
import org.storm.core.graphics.Resolution

object CoreContext {
    const val RESOLUTION = "resolution"
    const val UNIT_CONVERTOR = "unitConvertor"
    const val JSON_MAPPER = "jsonMapper"
    const val YAML_MAPPER = "yamlMapper"
    const val XML_MAPPER = "xmlMapper"
}

/**
 * Loads default mappers for JSON, YAML and XML to help with serialization and deserialization of data
 */
fun Context.loadMappers() {
    this.update(mapOf(
        CoreContext.JSON_MAPPER to jacksonObjectMapper(),
        CoreContext.YAML_MAPPER to YAMLMapper()
            .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerKotlinModule(),
        CoreContext.XML_MAPPER to XmlMapper()
            .registerKotlinModule()
    ))
}

/**
 * @return A data mapper which can read, convert and write JSON
 */
val Context.JSON_MAPPER get() = settings[CoreContext.JSON_MAPPER] as ObjectMapper

/**
 * @return A data mapper which can read, convert and write YAML
 */
val Context.YAML_MAPPER get() = settings[CoreContext.YAML_MAPPER] as ObjectMapper

/**
 * @return A data mapper which can read, convert and write XML
 */
val Context.XML_MAPPER get() = settings[CoreContext.YAML_MAPPER] as ObjectMapper

/**
 * @return The current unit convertor used for converting between pixels and game engine units.
 */
val Context.UNIT_CONVERTOR get() = settings[CoreContext.UNIT_CONVERTOR] as? UnitConvertor ?: UnitConvertor.DEFAULT

/**
 * @return The current resolution of the game window.
 */
val Context.RESOLUTION: Resolution get() = settings[CoreContext.RESOLUTION] as? Resolution ?: Resolution.SD

/**
 * @return The current resolution of the game window in game units
 */
val Context.RESOLUTION_IN_UNITS: Resolution get() = Resolution(RESOLUTION.width.units, RESOLUTION.height.units)

/**
 * Set the resolution of the game window.
 *
 * @param resolution The new resolution to set.
 * @param schedule Whether to schedule the update or apply it immediately (default false).
 */
fun Context.setResolution(resolution: Resolution, schedule: Boolean = false) {
    if (resolution == this.RESOLUTION) return

    update(mapOf(CoreContext.RESOLUTION to resolution), schedule)
}

/**
 * Sets the number of pixels per game engine unit. This is used to convert between pixels and units.
 * This change will ALWAYS be scheduled to the beginning of the next game loop to avoid potential race conditions. Avoid
 * updating this value dynamically within your game as it has implications on physics and rendering. It should be set once.
 *
 * @param ppu the number of pixels per unit
 */
fun Context.setPPU(ppu: Double) {
    update(mapOf(CoreContext.UNIT_CONVERTOR to UnitConvertor(ppu)), true)
}