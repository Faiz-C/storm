package org.storm.core.input

/**
 * A Translator translates one type into another.
 *
 * @param T the type to be translated from
 * @param U the type to be translated to */
fun interface Translator<T, U> {

  /**
   * @param t object to be translated
   * @return returns the translation of the given object to the desired type
   */
  fun translate(t: T): U

}
