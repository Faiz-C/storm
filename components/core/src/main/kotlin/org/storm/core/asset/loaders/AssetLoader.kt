package org.storm.core.asset.loaders

interface AssetLoader {
    val extensions: List<String>
    fun <T> load(path: String, clazz: Class<T>): T
}
