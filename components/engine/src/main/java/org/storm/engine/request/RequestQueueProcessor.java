package org.storm.engine.request;

import org.storm.core.input.action.ActionManager;

/**
 * A RequestQueueProcessor is similar to a Processor in its responsibility but differs in when the corresponding action
 * will take place. This processor will place its wanted actions onto the given RequestQueue for the StormEngine to handle
 * during its game loop.
 */
public interface RequestQueueProcessor {

  /**
   * Determines what actions should be taken based on what actions the User performs and places them onto the given
   * RequestQueue.
   * @param actionManager an ActionManager which details what in game actions the User is currently performing
   * @param requestQueue the RequestQueue that the StormEngine will use to handle input requests
   */
  void process(ActionManager actionManager, RequestQueue requestQueue);

}
