package org.storm.core.input

data class Action(
    val input: Input,
    val context: MutableMap<String, Any?> = mutableMapOf()
)
