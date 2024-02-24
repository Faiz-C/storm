package org.storm.core.asset.source.context

/**
 * AssetContextBuilders are used to build a context for an AssetSource.
 */
interface AssetContextBuilder {

    /**
     * Builds the context for an AssetSource.
     *
     * @return The context for an AssetSource.
     */
    fun build(): Map<String, String>

}
