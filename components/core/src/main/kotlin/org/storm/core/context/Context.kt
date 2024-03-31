package org.storm.core.context

import org.storm.core.utils.observation.Observable

/**
 * The Context object encapsulates the settings for the game. It is observable, so that any changes to the settings
 * can be observed by other components, updating them if necessary. The settings are stored as a simple map to allow
 * for easy flexibility and creation of extension functions.
 */
object Context: Observable() {
    private val mutableSettings: MutableMap<String, Any> = mutableMapOf()
    private val updates: MutableSet<(Map<String, Any>) -> Map<String, Any>> = mutableSetOf()

    /**
     * @return Current settings for the context.
     */
    var SETTINGS: Map<String, Any> = this.mutableSettings
        private set

    /**
     * Update the settings for the context. If schedule is false, then the update will be applied immediately,
     * otherwise it will be run at the start of the next game loop iteration.
     *
     * Note: Any updates which take place while the game loop is running should be scheduled to avoid potential
     * concurrency issues.
     *
     * @param updater A function that takes the current settings and returns updated settings.
     * @param schedule Whether to schedule the update or apply it immediately (default false).
     */
    fun update(schedule: Boolean = false, updater: (Map<String, Any>) -> Map<String, Any>) {
        if (schedule) {
            this.updates.add(updater)
            return
        }

        this.SETTINGS = updater(this.mutableSettings)
        this.updateObservers()
    }

    /**
     * Run all scheduled updates to update the context.
     */
    fun runScheduledUpdates() {
        this.SETTINGS = this.updates.fold(mutableSettings.toMap()) { acc, updater ->
            acc + updater(mutableSettings)
        }
        this.updateObservers()
    }
}
