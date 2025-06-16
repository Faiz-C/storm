package org.storm.core.input

/**
 * InputBindings help translate an InputState into an ActionState for the game engine components to utilize when updating
 * in game state.
 */
fun interface InputTranslator {

    /**
     * @param inputState state of inputs currently active within the game engine
     * @return returns the state of active actions within the game engine based on the given InputState
     */
    fun getActionState(inputState: InputState): ActionState

}