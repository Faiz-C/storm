package org.storm.core.asset.source.context

class LocalStorageAssetContextBuilder(
    private val assetDir: String,
    private val assetFileExt: String
): AssetContextBuilder {

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
