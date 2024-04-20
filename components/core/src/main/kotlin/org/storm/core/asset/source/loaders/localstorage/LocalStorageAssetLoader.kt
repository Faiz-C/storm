package org.storm.core.asset.source.loaders.localstorage

import com.fasterxml.jackson.core.type.TypeReference
import org.storm.core.asset.source.loaders.AssetLoader
import org.storm.core.asset.source.types.LocalStorageAssetSource
import org.storm.core.exception.AssetException
import java.io.File

/**
 * A LocalStorageAssetLoader is responsible for loading assets from a local storage source.
 */
abstract class LocalStorageAssetLoader(
    protected val extensions: Set<String>
) : AssetLoader {

    override fun <T> load(
        assetType: String,
        assetId: String,
        context: Map<String, String>,
        typeRef: TypeReference<T>
    ): T? {
        val assetDir = getAssetDir(context)

        this.extensions.forEach { ext ->
            val file = File(createFilePath(assetDir, assetType, assetId, ext))

            if (!file.exists()) return@forEach

            return load(file, typeRef)
        }

        return null
    }

    abstract fun <T> load(file: File, typeRef: TypeReference<T>): T?

    private fun createFilePath(assetDir: String, assetType: String, assetId: String, fileExt: String): String {
        return "$assetDir/$assetType/$assetId.$fileExt"
    }

    private fun getAssetDir(context: Map<String, String>): String {
        return context[LocalStorageAssetSource.CONTEXT_DIR_FIELD]
            ?: throw AssetException("No base asset directory supplied in context. Context: $context")
    }
}
