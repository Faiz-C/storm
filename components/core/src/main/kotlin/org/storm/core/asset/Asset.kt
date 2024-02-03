package org.storm.core.asset

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class Asset(
    val type: String,
    val impl: String
)
