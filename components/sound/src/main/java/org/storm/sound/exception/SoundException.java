package org.storm.sound.exception;

/**
 * A standard sound exception
 */
public class SoundException extends RuntimeException {

  public SoundException(String msg) {
    super(msg);
  }

  public SoundException(String msg, Exception e) {
    super(msg, e);
  }

}
