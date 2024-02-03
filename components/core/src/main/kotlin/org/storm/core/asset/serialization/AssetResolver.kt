package org.storm.core.asset.serialization

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.storm.core.asset.Asset
import org.storm.core.exception.AssetException

class AssetResolver: TypeIdResolverBase() {

    companion object {
        private val ASSET_PACKAGES: MutableList<String> = mutableListOf("org.storm")

        fun includePackage(packageName: String) {
            ASSET_PACKAGES.add(packageName)
        }
    }

    private val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages(*ASSET_PACKAGES.toTypedArray())
            .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
    )

    private val assetTypes: Set<Class<*>> = reflections.get(Scanners.TypesAnnotated.of(Asset::class.java).asClass<Asset>())

    private lateinit var superType: JavaType

    override fun init(baseType: JavaType) {
        superType = baseType
    }

    override fun idFromValue(value: Any): String {
        return idFromValueAndType(value, value.javaClass)
    }

    override fun idFromValueAndType(value: Any, suggestedType: Class<*>): String {
        // The class needs to be annotated with the Asset annotation to allow for auto resolution
        return getAssetType(suggestedType)
    }

    override fun typeFromId(context: DatabindContext, type: String): JavaType {
        return getClassForType(type)?.let {
            context.constructSpecializedType(superType, it)
        } ?: throw AssetException("Failed to resolve implementation class for type $type")
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        return JsonTypeInfo.Id.NAME
    }

    private fun getAssetType(clazz: Class<*>): String {
        val assetDetails = clazz.getAnnotation(Asset::class.java)
            ?: throw AssetException("Asset annotation not found on ${clazz.simpleName}, cannot auto resolve.")

        return "${assetDetails.type}-${assetDetails.impl}"
    }

    private fun getClassForType(type: String): Class<*>? {
        return assetTypes.firstOrNull {
            val annotation = it.getAnnotation(Asset::class.java)
            "${annotation.type}-${annotation.impl}" == type
        }
    }
}
