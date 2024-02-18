package org.storm.core.asset.source.context

interface AssetContextBuilder {
    fun build(): Map<String, String>
}
