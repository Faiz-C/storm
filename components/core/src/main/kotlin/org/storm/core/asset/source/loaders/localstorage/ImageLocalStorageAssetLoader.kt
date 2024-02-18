package org.storm.core.asset.source.loaders.localstorage

import com.fasterxml.jackson.core.type.TypeReference
import javafx.scene.image.Image
import org.storm.core.asset.source.context.LocalStorageAssetContextBuilder
import org.storm.core.exception.AssetException
import java.io.FileInputStream

/**
 * This is a special asset loader as it specializes in loading images into one specific implementation.
 */
class ImageLocalStorageAssetLoader : LocalStorageAssetLoader(setOf("png", "jpeg", "jpg")) {

    @Suppress("UNCHECKED_CAST")
    override fun <T> load(assetId: String, context: Map<String, Any>, typeRef: TypeReference<T>): T {

        // TODO: abstract this to a Image interface/class and avoid directly referencing JavaFX Image
        if (typeRef.type.javaClass != Image::class.java) {
            throw AssetException("ImageLocalStorageAssetLoader can only load assets into Image instances")
        }

        val path = createFilePath(assetId, context)

        return Image(FileInputStream(path)) as T // Safe cast because of the check above
    }
}
