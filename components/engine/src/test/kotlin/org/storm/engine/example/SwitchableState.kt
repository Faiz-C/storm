package org.storm.engine.example

import org.storm.core.asset.AssetManager
import org.storm.core.context.Context
import org.storm.core.input.ActionState
import org.storm.engine.KeyActionConstants
import org.storm.engine.context.REQUEST_QUEUE
import org.storm.engine.request.types.StateChangeRequest
import org.storm.engine.state.GameState
import org.storm.sound.manager.SoundManager

abstract class SwitchableState(assetManager: AssetManager) : GameState(SoundManager(assetManager = assetManager)) {

    override suspend fun process(actionState: ActionState) {
        if (actionState.isFirstTrigger(KeyActionConstants.ONE)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.ONE))
        } else if (actionState.isFirstTrigger(KeyActionConstants.TWO)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.TWO))
        } else if (actionState.isFirstTrigger(KeyActionConstants.THREE)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.THREE))
        } else if (actionState.isFirstTrigger(KeyActionConstants.FOUR)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.FOUR))
        } else if (actionState.isFirstTrigger(KeyActionConstants.FIVE)) {
            Context.REQUEST_QUEUE.submit(StateChangeRequest(KeyActionConstants.FIVE))
        }
    }

}
