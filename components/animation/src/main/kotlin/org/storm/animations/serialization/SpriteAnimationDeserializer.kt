package org.storm.animations.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.storm.animations.sprite.SpriteAnimation
import org.storm.animations.sprite.SpriteSheet

class SpriteAnimationDeserializer: JsonDeserializer<SpriteAnimation>() {
    override fun deserialize(jp: JsonParser, context: DeserializationContext): SpriteAnimation {
        val node = jp.codec.readTree<JsonNode>(jp)

        // Standard Animation properties
        val delay = node.get("delay").asInt()
        val loops = node.get("loops").asInt()

        // Sprite Animation specific properties
        val spriteSheet = node.get("sprites").get("sheet").get("file").asText() // which sprite sheet file to use
        val width = node.get("sprites").get("sheet").get("width").asInt() // width of each sprite
        val height = node.get("sprites").get("sheet").get("height").asInt() // width of each sprite
        val row = node.get("sprites").get("row").asInt() // which row in the sprite sheet to use

        val sprites = SpriteSheet(spriteSheet, width, height).row(row)

        return SpriteAnimation(sprites = sprites, delay = delay, loops = loops)
    }
}
