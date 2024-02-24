package org.storm.core.asset.source.context

/**
 * LocalStorageAssetContextBuilder is used to build the context for loading assets from local storage. This context will
 * include the directory where the assets are located and the file extension of the assets at minimum.
 *
 * @param assetDir The directory where the assets are located.
 * @param assetFileExt The file extension of the assets.
 */
class LocalStorageAssetContextBuilder(
    private val assetDir: String,
    private val assetFileExt: String
) : AssetContextBuilder {

    companion object {
        const val DIR = "directory"
        const val EXT = "ext"
    }

    override fun build(): Map<String, String> {
        return mapOf(
            DIR to assetDir,
            EXT to assetFileExt
        )
    }
}
