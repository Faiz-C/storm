package org.storm.core.asset

import com.fasterxml.jackson.core.type.TypeReference
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.storm.core.asset.source.AssetSource
import org.storm.core.asset.source.loaders.AssetLoader
import org.storm.core.exception.AssetException
import java.time.Duration

class AssetManager(
    maxCacheSize: Long = 3000,
    cacheTtl: Duration = Duration.ofMinutes(10),
    private val useCache: Boolean = true,
) {

    // A map of asset loaders for each storage source
    private val assetSources: MutableMap<String, AssetSource> = mutableMapOf()

    // A cache which links asset paths to their loaded objects
    private val assetCache: Cache<String, Any> = Caffeine.newBuilder()
        .maximumSize(maxCacheSize)
        .expireAfterWrite(cacheTtl)
        .build()

    fun registerSource(source: AssetSource) {
        assetSources[source.id] = source
    }

    fun registerLoader(assetSourceId: String, assetLoader: AssetLoader) {
        val existingSource = assetSources[assetSourceId]
            ?: throw AssetException("No AssetSource found for id $assetSourceId")

        assetSources[assetSourceId] = existingSource.copy(loaders = existingSource.loaders.plus(assetLoader))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAsset(
        assetId: String,
        sourceId: String,
        typeRef: TypeReference<T>,
        updateContext: (MutableMap<String, String>) -> Unit = {}
    ): T {
        val cached = if (useCache) {
            assetCache.getIfPresent(assetId) as? T
        } else {
            null
        }

        return cached ?: run {
            val source = assetSources[sourceId]
                ?: throw AssetException("No AssetSource found for id $sourceId")

            val context = source.contextBuilder
                .build()
                .toMutableMap()

            updateContext(context)

            val loader = assetSources[sourceId]?.loaders?.firstOrNull {
                it.supports(assetId, context)
            } ?: throw AssetException("No AssetLoader found for source $sourceId which supports loading asset with id $assetId and context: $context")

            assetCache.get(assetId) {
                loader.load(assetId, context, typeRef)
            } as T
        }
    }

    inline fun <reified T> getAsset(
        assetId: String,
        sourceId: String,
        noinline updateContext: (MutableMap<String, String>) -> Unit = {}
    ): T {
        return getAsset(assetId, sourceId, object: TypeReference<T>() {}, updateContext)
    }
}
