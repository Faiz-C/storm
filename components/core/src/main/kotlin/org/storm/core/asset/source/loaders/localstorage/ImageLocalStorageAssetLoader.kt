package org.storm.core.asset.source.loaders.localstorage

import com.fasterxml.jackson.core.type.TypeReference
import javafx.scene.image.Image
import org.storm.core.exception.AssetException
import java.io.File
import java.io.FileInputStream

/**
 * An AssetLoader that loads images from the local storage. This is a special loader that only has one supported type,
 * Image.
 */
class ImageLocalStorageAssetLoader : LocalStorageAssetLoader(setOf("png", "jpeg", "jpg")) {

    @Suppress("UNCHECKED_CAST")
    override fun <T> load(file: File, typeRef: TypeReference<T>): T? {
        // TODO: abstract this to a Image interface/class and avoid directly referencing JavaFX Image
        if (typeRef.type.javaClass != Image::class.java) {
            throw AssetException("ImageLocalStorageAssetLoader can only load assets into Image instances")
        }

        return Image(FileInputStream(file)) as T // Safe cast because of the check above
    }

}
