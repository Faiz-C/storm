package org.storm.core.asset.serialization

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import org.storm.core.asset.AssetManager
import org.storm.core.exception.AssetException

class AssetResolver: TypeIdResolverBase() {

    private val assetManager: AssetManager = AssetManager()
    private lateinit var superType: JavaType

    override fun init(baseType: JavaType) {
        superType = baseType
    }

    override fun idFromValue(value: Any): String {
        return idFromValueAndType(value, value.javaClass)
    }

    override fun idFromValueAndType(value: Any, suggestedType: Class<*>): String {
        // The class needs to be annotated with the Asset annotation to allow for auto resolution
        return assetManager.getAssetType(suggestedType)
    }

    override fun typeFromId(context: DatabindContext, type: String): JavaType {
        return assetManager.getAssetForType(type)?.let {
            context.constructSpecializedType(superType, it)
        } ?: throw AssetException("Failed to resolve implementation class for type $type")
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        return JsonTypeInfo.Id.NAME
    }
}
