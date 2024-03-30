package org.storm.core.utils.observation

abstract class Observable {
    private val observers: MutableSet<Observer> = mutableSetOf()

    fun addObserver(o: Observer) {
        this.observers.add(o)
    }

    fun removeObserver(o: Observer) {
        this.observers.remove(o)
    }

    fun updateObservers() {
        this.observers.forEach {
            it.update(this)
        }
    }
}
