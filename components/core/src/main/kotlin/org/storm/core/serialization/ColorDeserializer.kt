package org.storm.core.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.storm.core.graphics.canvas.Color

class ColorDeserializer: JsonDeserializer<Color>() {

    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext
    ): Color {
        val node: JsonNode = parser.codec.readTree(parser)

        return when {
            node.isTextual -> parseHexColor(node.asText())
            node.isArray -> parseArrayColor(node)
            node.isObject -> parseObjectColor(node)
            else -> throw IllegalArgumentException("Invalid color format")
        }
    }

    private fun parseHexColor(hex: String): Color {
        val cleanHex = hex.removePrefix("#")
        val rgb = cleanHex.toLong(16).toInt()

        return when (cleanHex.length) {
            6 -> Color(
                red = ((rgb shr 16) and 0xFF).toDouble(),
                green = ((rgb shr 8) and 0xFF).toDouble(),
                blue = (rgb and 0xFF).toDouble(),
                alpha = 255.0
            )
            8 -> Color(
                red = ((rgb shr 24) and 0xFF).toDouble(),
                green = ((rgb shr 16) and 0xFF).toDouble(),
                blue = ((rgb shr 8) and 0xFF).toDouble(),
                alpha = (rgb and 0xFF).toDouble()
            )
            else -> throw IllegalArgumentException("Invalid hex color: $hex")
        }
    }

    private fun parseArrayColor(node: JsonNode): Color {
        val values = (0 until node.size()).map { node[it].asDouble() }

        return when (values.size) {
            3 -> Color(values[0], values[1], values[2], 255.0)
            4 -> Color(values[0], values[1], values[2], values[3])
            else -> throw IllegalArgumentException("Color array must have 3 or 4 elements")
        }
    }

    private fun parseObjectColor(node: JsonNode): Color {
        return Color(
            red = node.get("red")?.asDouble() ?: 0.0,
            green = node.get("green")?.asDouble() ?: 0.0,
            blue = node.get("blue")?.asDouble() ?: 0.0,
            alpha = node.get("alpha")?.asDouble() ?: 255.0
        )
    }

}