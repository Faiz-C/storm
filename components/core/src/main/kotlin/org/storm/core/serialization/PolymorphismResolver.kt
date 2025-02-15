package org.storm.core.serialization

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder

/**
 * PolymorphismResolver is used with Jackson to serialize and deserialize polymorphic objects.
 *
 * It uses reflection to scan for classes annotated with @Polymorphic to identify available implementations. These types
 * are then used to resolve incoming data to the correct implementation during deserialization. For serialization, this
 * resolver will add a field to all serialized objects matching the property value from @JsonTypeInfo which has
 * it's value derived from the Polymorphic annotation.
 *
 * Example usage:
 * ```
 * @JsonTypeInfo(
 *     use = JsonTypeInfo.Id.NAME,
 *     include = JsonTypeInfo.As.PROPERTY,
 *         property = "type",
 *         visible = true
 *     )
 * @JsonTypeIdResolver(PolymorphismResolver::class)
 * @JsonIgnoreProperties("type") // So that your implementations don't need to define this property
 * interface TheInterface
 * ```
 */
class PolymorphismResolver : TypeIdResolverBase() {

    companion object {
        private val ASSET_PACKAGES: MutableList<String> = mutableListOf("org.storm")

        /**
         * Adds a package to the list of packages to scan for Asset classes
         *
         * @param packageName The package name to scan for assets
         */
        fun includePackage(packageName: String) {
            ASSET_PACKAGES.add(packageName)
        }
    }

    private val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages(*ASSET_PACKAGES.toTypedArray())
            .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
    )

    private val polymorphicTypes: Set<Class<*>> =
        reflections.get(Scanners.TypesAnnotated.of(Polymorphic::class.java).asClass<Polymorphic>())

    private lateinit var superType: JavaType

    override fun init(baseType: JavaType) {
        this.superType = baseType
    }

    override fun idFromValue(value: Any): String {
        return idFromValueAndType(value, value.javaClass)
    }

    override fun idFromValueAndType(value: Any, suggestedType: Class<*>): String {
        return suggestedType.getAnnotation(Polymorphic::class.java).type
    }

    override fun typeFromId(context: DatabindContext, type: String?): JavaType {
        return getClassForType(type)?.let {
            if (this::superType.isInitialized) {
                context.constructSpecializedType(superType, it)
            } else {
                context.constructType(it)
            }
        } ?: throw IllegalStateException("Failed to resolve type for $type")
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        return JsonTypeInfo.Id.NAME
    }

    private fun getClassForType(type: String?): Class<*>? {
        return polymorphicTypes.firstOrNull {
            val annotation = it.getAnnotation(Polymorphic::class.java)
            annotation.type == type
        }
    }
}