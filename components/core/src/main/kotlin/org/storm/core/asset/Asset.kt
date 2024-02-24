package org.storm.core.asset

/**
 * An annotation used to mark a class as an asset. Assets are loadable resources that can be used in a game.
 *
 * @param type The type of asset this class represents. E.g., "animation"
 * @param impl The implementation of the asset. E.g., "sprite"
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class Asset(
    val type: String,
    val impl: String = "default"
)
