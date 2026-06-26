package org.storm.engine.events

import org.storm.engine.state.GameState

sealed interface EngineEvent {
    data class StateChange(val targetStateId: String? = null, val targetState: GameState? = null): EngineEvent
    data class TogglePhysics(val enabled: Boolean? = null): EngineEvent
}