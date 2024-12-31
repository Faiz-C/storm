package org.storm.core.input

/**
 * InputBindings help translate a type of input T into an action (String).
 *
 * @param T the type of input to be translated from
 */
fun interface InputBindings<T> {

    /**
     * @param input input object to be translated to an action
     * @return returns the action for a given input if it is bound to any, null if no binding is found
     */
    fun getAction(input: T): String?

}