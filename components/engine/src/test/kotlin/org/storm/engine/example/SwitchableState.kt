package org.storm.engine.example

import org.storm.core.event.EventManager
import org.storm.core.input.Action
import org.storm.core.input.ActionState
import org.storm.core.input.InputState
import org.storm.engine.Controls
import org.storm.engine.Controls.FIVE
import org.storm.engine.Controls.FOUR
import org.storm.engine.Controls.ONE
import org.storm.engine.Controls.THREE
import org.storm.engine.Controls.TWO
import org.storm.engine.events.EngineEvent
import org.storm.engine.events.getEngineEventStream
import org.storm.engine.state.GameState

abstract class SwitchableState : GameState {

    override suspend fun process(actionState: ActionState) {
        if (actionState.isFirstActivation(ONE)) {
            EventManager.getEngineEventStream().produce(EngineEvent.StateChange(targetStateId = ONE))
        } else if (actionState.isFirstActivation(TWO)) {
            EventManager.getEngineEventStream().produce(EngineEvent.StateChange(targetStateId = TWO))
        } else if (actionState.isFirstActivation(THREE)) {
            EventManager.getEngineEventStream().produce(EngineEvent.StateChange(targetStateId = THREE))
        } else if (actionState.isFirstActivation(FOUR)) {
            EventManager.getEngineEventStream().produce(EngineEvent.StateChange(targetStateId = FOUR))
        } else if (actionState.isFirstActivation(FIVE)) {
            EventManager.getEngineEventStream().produce(EngineEvent.StateChange(targetStateId = FIVE))
        }
    }

    override fun getActionState(inputState: InputState): ActionState {
        val activeActions = inputState.activeInputs
            .filterKeys {
                Controls.BINDINGS.contains(it)
            }.map { (key, value) ->
                Controls.BINDINGS[key]!! to Action(value)
            }.toMap()

        // Just for validating mouse inputs
        //inputState.activeInputs["mouse-moved"]?.let {
        //    val input = it.rawInput as MouseEvent
        //    val pos = Point(input.x, input.y)
        //    println("Mouse position: $pos, ${it.lastUpdateTime}")
        //}

        return ActionState(activeActions)
    }

}
