package org.storm.core.utils.observation

interface Observer {
    fun update(o: Observable)
}
