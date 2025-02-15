package org.storm.core.serialization

/**
 * This annotation is used in conjunction with the PolymorphismResolver to help with general polymorphic serialization and deserialization of objects.
 * Simply adding this annotation to the IMPLEMENTATION class of a polymorphic hierarchy will set it up for deserialization and serialization.
 *
 * @param type A unique identifier for the implementation class (should not be the class name or the class path)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Polymorphic(val type: String)
