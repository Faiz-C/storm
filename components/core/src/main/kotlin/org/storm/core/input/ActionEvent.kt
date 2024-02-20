package org.storm.core.input

data class ActionEvent(
    val action: String,
    val active: Boolean
)
