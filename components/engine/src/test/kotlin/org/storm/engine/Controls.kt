package org.storm.engine

import javafx.scene.input.KeyCode

object Controls {
    const val ONE = "1"
    const val TWO = "2"
    const val THREE = "3"
    const val FOUR = "4"
    const val FIVE = "5"
    const val W = "w"
    const val S = "s"
    const val A = "a"
    const val D = "d"
    const val SPACE = "pause"

    val BINDINGS = mapOf(
        KeyCode.SPACE.name to SPACE,
        KeyCode.NUMPAD1.name to ONE,
        KeyCode.NUMPAD2.name to TWO,
        KeyCode.NUMPAD3.name to THREE,
        KeyCode.NUMPAD4.name to FOUR,
        KeyCode.NUMPAD5.name to FIVE,
        KeyCode.W.name to W,
        KeyCode.S.name to S,
        KeyCode.A.name to A,
        KeyCode.D.name to D
    )
}


