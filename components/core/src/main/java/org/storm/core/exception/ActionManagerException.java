package org.storm.core.exception;

/**
 * Generic runtime exception to highlight an issue with the ActionManager
 */
public class ActionManagerException extends RuntimeException {
  public ActionManagerException(String msgFmt, Object... args) {
    super(String.format(msgFmt, args));
  }
}
