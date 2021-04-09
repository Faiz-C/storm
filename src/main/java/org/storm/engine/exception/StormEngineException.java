package org.storm.engine.exception;

/**
 * Standard Runtime Exception used for issues within the StormEngine
 */
public class StormEngineException extends RuntimeException {

  public StormEngineException(String msg) {
    super(msg);
  }

  public StormEngineException(String msg, Exception e) {
    super(msg, e);
  }

}
