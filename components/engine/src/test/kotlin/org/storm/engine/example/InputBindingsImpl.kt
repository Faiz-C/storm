package org.storm.engine.example

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import org.storm.core.input.InputBindings
import org.storm.engine.KeyActionConstants

class InputBindingsImpl : InputBindings<KeyEvent> {

    override fun getAction(input: KeyEvent): String? {
        return when (input.code) {
            KeyCode.SPACE -> KeyActionConstants.SPACE
            KeyCode.NUMPAD1 -> KeyActionConstants.ONE
            KeyCode.NUMPAD2 -> KeyActionConstants.TWO
            KeyCode.NUMPAD3 -> KeyActionConstants.THREE
            KeyCode.NUMPAD4 -> KeyActionConstants.FOUR
            KeyCode.W -> KeyActionConstants.W
            KeyCode.S -> KeyActionConstants.S
            KeyCode.A -> KeyActionConstants.A
            KeyCode.D -> KeyActionConstants.D
            else -> ""
        }
    }
}
