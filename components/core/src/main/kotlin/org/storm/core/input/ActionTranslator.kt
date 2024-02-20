package org.storm.core.input

/**
 * An ActionTranslator translates a type of input T into an action (String)
 *
 * @param T the type of input to be translated from
 */
fun interface ActionTranslator<T> {

  /**
   * @param t input object to be translated
   * @return returns the translation of the given input type to an action
   */
  fun translate(t: T): String

}
