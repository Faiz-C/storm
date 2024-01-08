package org.storm.core.asset

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.storm.core.asset.loaders.AssetLoader
import org.storm.core.asset.loaders.JsonAssetLoader
import org.storm.core.asset.loaders.YamlAssetLoader
import org.storm.core.exception.AssetException
import java.time.Duration

class AssetManager(
    maxCacheSize: Long = 3000,
    cacheTtl: Duration = Duration.ofMinutes(10),
    private val assetDir: String = "assets",
    private val useCache: Boolean = true,
) {

    companion object {
        private val ASSET_LOADERS = mutableListOf(
            JsonAssetLoader(),
            YamlAssetLoader()
        )

        fun registerLoader(loader: AssetLoader) {
            ASSET_LOADERS.add(loader)
        }
    }

    // A cache which links asset paths to their loaded objects
    private val cache: Cache<String, Any> = Caffeine.newBuilder()
        .maximumSize(maxCacheSize)
        .expireAfterWrite(cacheTtl)
        .build()

    @Suppress("UNCHECKED_CAST")
    fun <T> getAsset(assetPath: String, clazz: Class<T>): T {
        val fullAssetPath = "$assetDir/$assetPath"
        val ext = fullAssetPath.substringAfterLast(".")

        return if (useCache) {
            val a = cache.get(fullAssetPath) {
                loadAsset(fullAssetPath, ext, clazz)
            } as T
            a
        } else {
            loadAsset(fullAssetPath, ext, clazz)
        }
    }

    private fun <T> loadAsset(assetPath: String, assetExt: String, clazz: Class<T>): T {
        val loader = ASSET_LOADERS.find { it.extensions.contains(assetExt) }
            ?: throw AssetException("No loader found for asset type ${assetExt}.")

        return loader.load(assetPath, clazz)
    }
}
