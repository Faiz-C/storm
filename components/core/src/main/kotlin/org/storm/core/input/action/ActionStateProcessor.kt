package org.storm.core.input.action

/**
 * An ActionStateProcessor processes a given ActionState and updates the state of the game accordingly. I.e. if the
 * state of a JUMP action is true then after processing the player could start jumping within the game.
 */
fun interface ActionStateProcessor {

    /**
     * Processes the given ActionState and updates the state of the game accordingly.
     *
     * @param actionState ActionState to process
     */
    suspend fun process(actionState: ActionState)

}
