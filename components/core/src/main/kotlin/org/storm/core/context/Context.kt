package org.storm.core.context

import org.storm.core.event.EventManager
import org.storm.core.event.EventStream

/**
 * The Context object encapsulates the settings/global state for the game. The settings are stored as a simple map to allow
 * for easy flexibility and creation of extension functions. Upon changes to the inner settings the Context will emit
 * ContextChange events which can be listened to by any component.
 */
object Context {

    private val updates: MutableList<Map<String, Any>> = mutableListOf()

    /**
     * @return Current settings for the context.
     */
    var settings: Map<String, Any> = mapOf()
        private set

    /**
     * Update the settings for the context. If schedule is false, then the update will be applied immediately,
     * otherwise it will be run at the start of the next game loop iteration.
     *
     * Note: Any updates which take place while the game loop is running should be scheduled to avoid potential
     * concurrency issues.
     *
     * @param settingsToUpdate A map of settings to update
     * @param schedule Whether to schedule the update or apply it immediately (default false).
     */
    fun update(settingsToUpdate: Map<String, Any>, schedule: Boolean = false) {
        if (schedule) {
            this.updates.add(settingsToUpdate)
            return
        }

        processUpdates(listOf(settingsToUpdate))?.let {
            EventManager.getContextEventStream().produce(it)
        }
    }

    /**
     * Run all scheduled updates to update the context.
     */
    fun runScheduledUpdates() {
        processUpdates(this.updates)?.let {
            EventManager.getContextEventStream().produce(it)
        }

        this.updates.clear()
    }

    /**
     * Processes the list of setting updates in order and validates changes to the settings. If settings of the context
     * change then an event object is returned.
     *
     * @param updates List of setting changes to apply on top of the existing settings
     * @return ContextEvent if settings changed, null if nothing changed
     */
    private fun processUpdates(updates: List<Map<String, Any>>): ContextEvent? {
        val oldSettings = this.settings.toMap()
        val newSettings = updates.fold(oldSettings) { acc, settingsToUpdate ->
            acc + settingsToUpdate
        }

        if (oldSettings == newSettings) return null

        this.settings = newSettings

        return ContextEvent(oldSettings, newSettings)
    }
}
