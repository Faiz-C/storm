package org.storm.core.asset

import com.fasterxml.jackson.core.type.TypeReference
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.storm.core.asset.source.loaders.AssetLoader
import org.storm.core.asset.source.loaders.AssetSource
import org.storm.core.exception.AssetException
import java.time.Duration

/**
 * The AssetManager is responsible for managing the loading and caching of assets from various sources.
 */
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

    /**
     * Registers a new AssetSource with the AssetManager
     *
     * @param source The AssetSource to register
     */
    fun registerSource(source: AssetSource) {
        assetSources[source.id] = source
    }

    /**
     * Retrieves an asset. If caching is enabled, the asset will be loaded from the cache if it exists, otherwise
     * it will be loaded from the source and cached.
     *
     * @param assetId The id of the asset to retrieve
     * @param assetSourceId The id of the AssetSource to retrieve the asset from
     * @param typeRef The type reference of the asset to retrieve, used for deserialization if needed
     * @return The asset
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getAsset(
        assetType: String,
        assetId: String,
        assetSourceId: String,
        typeRef: TypeReference<T>
    ): T {
        val cached = if (useCache) {
            assetCache.getIfPresent(assetId) as? T
        } else {
            null
        }

        return cached ?: run {
            val source = assetSources[assetSourceId]
                ?: throw AssetException("No AssetSource found for id $assetSourceId")

            assetCache.get(assetId) {
                source.loadAsset(assetType, assetId, typeRef)
            } as T
        }
    }

    /**
     * Retrieves an asset. If caching is enabled, the asset will be loaded from the cache if it exists, otherwise
     * it will be loaded from the source and cached.
     *
     * @param assetId The id of the asset to retrieve
     * @param assetSourceId The id of the AssetSource to retrieve the asset from
     * @return The asset
     */
    inline fun <reified T> getAsset(
        assetType: String,
        assetId: String,
        assetSourceId: String,
    ): T {
        return getAsset(assetType, assetId, assetSourceId, object : TypeReference<T>() {})
    }
}
