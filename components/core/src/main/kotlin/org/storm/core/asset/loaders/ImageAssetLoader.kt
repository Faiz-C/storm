package org.storm.core.asset.loaders

import javafx.scene.image.Image
import java.io.FileInputStream

class ImageAssetLoader(override val extensions: List<String> = listOf("png", "jpeg", "jpg")) : AssetLoader {

    override fun load(path: String, clazz: Class<*>): Any {
        return Image(FileInputStream(path))
    }

}
