package org.storm.core.asset.loaders

interface AssetLoader {
    val extensions: List<String>
    fun load(path: String, clazz: Class<*>): Any
}
