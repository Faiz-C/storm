package org.storm.core.asset

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.storm.core.asset.loaders.AssetLoader
import org.storm.core.asset.loaders.ImageAssetLoader
import org.storm.core.asset.loaders.JsonAssetLoader
import org.storm.core.asset.loaders.YamlAssetLoader
import org.storm.core.exception.AssetException
import java.io.File
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
            YamlAssetLoader(),
            ImageAssetLoader()
        )

        private val loadableExtensions get() = ASSET_LOADERS.flatMap { it.extensions }

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
    fun <T> getAsset(assetName: String, clazz: Class<T>, assetSubDir: String = "", assetExt: String = ""): T {

        // Build the full asset path
        val assetPathWithoutExt = "$assetDir${File.separator}${if (assetSubDir.isBlank()) "" else "$assetSubDir${File.separator}"}$assetName"
        val assetPath = if (assetExt.isBlank()) {
            // Best effort to find the correct extension using the registered loaders if no extension is provided
            val ext = loadableExtensions.firstOrNull { ext ->
                val path = "$assetPathWithoutExt.$ext"
                File(path).exists()
            } ?: throw AssetException("No extension provided and no file with name $assetName exists with supported file extensions $loadableExtensions.")

            "$assetPathWithoutExt.$ext"
        } else {
            "$assetPathWithoutExt.$assetExt"
        }

        return if (useCache) {
            return cache.get(assetName) {
                loadAsset(assetPath, clazz)
            } as T
        } else {
            loadAsset(assetPath, clazz)
        }
    }

    fun addAssets(assetName: String, vararg asset: Any) {
        asset.forEach {
            cache.put(assetName, it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> loadAsset(assetPath: String, clazz: Class<T>): T {
        val assetExt = assetPath.substringAfterLast(".")
        val loader = ASSET_LOADERS.find { it.extensions.contains(assetExt) }
            ?: throw AssetException("No loader found for asset type ${assetExt}.")

        return loader.load(assetPath, clazz) as T
    }
}
