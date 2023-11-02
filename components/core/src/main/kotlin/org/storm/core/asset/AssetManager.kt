package org.storm.core.asset

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.storm.core.exception.AssetException
import java.time.Duration

class AssetManager(
    maxCacheSize: Long = 3000,
    cacheTtl: Duration = Duration.ofMinutes(10),
    private val assetDir: String = "",
    private val useCache: Boolean = true,
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
) {
    companion object {
        private val ASSET_REGISTRY: MutableList<String> = mutableListOf("org.storm")

        fun registerAssetPackage(packageName: String) {
            ASSET_REGISTRY.add(packageName)
        }
    }

    private val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages(*ASSET_REGISTRY.toTypedArray())
            .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
    )

    private val assetTypes: Set<Class<*>> = reflections.get(Scanners.TypesAnnotated.of(Asset::class.java).asClass<Asset>())

    private val cache: LoadingCache<String, Any> = Caffeine.newBuilder()
        .maximumSize(maxCacheSize)
        .expireAfterWrite(cacheTtl)
        .refreshAfterWrite(cacheTtl)
        .build {
            ""
        }

    fun getAssetType(clazz: Class<*>): String {
        val assetDetails = clazz.getAnnotation(Asset::class.java)
            ?: throw AssetException("Asset annotation not found on ${clazz.simpleName}, cannot auto resolve.")

        return assetDetails.type
    }

    fun getAssetForType(type: String): Class<*>? {
        return assetTypes.firstOrNull {
            it.getAnnotation(Asset::class.java).type == type
        }
    }
}
