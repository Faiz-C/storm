package org.storm.core.utils.observation

/**
 * An Observer is an object that listens for changes in an Observable object.
 */
interface Observer {

    /**
     * Called when the Observable object has been updated
     *
     * @param o the Observable object that was updated
     */
    fun update(o: Observable)

}
