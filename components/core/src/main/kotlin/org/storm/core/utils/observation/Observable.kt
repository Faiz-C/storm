package org.storm.core.utils.observation

/**
 * An Observable object is an object that can be observed by multiple Observer objects. When updates occur to this object
 * it will notify all of its observers.
 */
abstract class Observable {
    private val observers: MutableSet<Observer> = mutableSetOf()

    /**
     * Adds an Observer to the list of Observers
     *
     * @param o the Observer to add
     */
    fun addObserver(o: Observer) {
        this.observers.add(o)
    }

    /**
     * Removes an Observer from the list of Observers
     *
     * @param o the Observer to remove
     */
    fun removeObserver(o: Observer) {
        this.observers.remove(o)
    }

    /**
     * Notifies all Observers that this object has been updated
     */
    fun updateObservers() {
        this.observers.forEach {
            it.update(this)
        }
    }
}
