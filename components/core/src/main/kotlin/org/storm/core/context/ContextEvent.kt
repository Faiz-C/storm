package org.storm.core.context

data class ContextEvent(
    val oldSettings: Map<String, Any>,
    val newSettings: Map<String, Any>
) {
    fun hasSettingChanged(setting: String): Boolean {
        return oldSettings[setting] != newSettings[setting]
    }
}
