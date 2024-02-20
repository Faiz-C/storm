package org.storm.engine.request

import org.storm.core.input.ActionState

/**
 * TODO: REVISIT THIS
 * A StormEngineProcessor is an alternative version of a Processor. They share the same responsibility but this extension
 * allows for submitting change requests to the StormEngine (i.e., toggling the physics on or off). The requests are placed
 * on the given queue and handled during the game loop.
 */
interface StormEngineProcessor {

  /**
   * Determines what actions should be taken based on what actions the User performs and places any additional requests
   * for the StormEngine onto the given RequestQueue.
   *
   * @param actionManager an ActionManager which details what in game actions the User is currently performing
   * @param requestQueue the RequestQueue that the StormEngine will use to handle input requests
   */
  fun process(actionState: ActionState, requestQueue: RequestQueue)

}
