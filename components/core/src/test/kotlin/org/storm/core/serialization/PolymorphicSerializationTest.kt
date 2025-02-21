package org.storm.core.serialization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.storm.core.context.Context
import org.storm.core.context.JSON_MAPPER
import org.storm.core.context.loadMappers

class PolymorphicSerializationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Context.loadMappers()
        }
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
    )
    @JsonTypeIdResolver(PolymorphismResolver::class)
    @JsonIgnoreProperties("type")
    interface TheInterface

    @Polymorphic("impl1")
    data class TheImpl1(val thing: String): TheInterface

    @Polymorphic("impl2")
    data class TheImpl2(val notThing: Int): TheInterface

    data class NoAnnotation(val i: Int)

    @Test
    fun testPolymorphicSerialization() {
        val impl1 = TheImpl1("hello")
        val json = Context.JSON_MAPPER.writeValueAsString(impl1)

        assert(json == "{\"type\":\"impl1\",\"thing\":\"hello\"}")

        val deserializedImpl1 = Context.JSON_MAPPER.readValue(json, TheInterface::class.java)

        assert(deserializedImpl1 == impl1)
    }

    @Test
    fun testNonPolymorphicSerialization() {
        val noAnnotation = NoAnnotation(1)
        val json = Context.JSON_MAPPER.writeValueAsString(noAnnotation)

        assert(json == "{\"i\":1}")

        val deserialized = Context.JSON_MAPPER.readValue(json, NoAnnotation::class.java)

        assert(deserialized == noAnnotation)
    }
}