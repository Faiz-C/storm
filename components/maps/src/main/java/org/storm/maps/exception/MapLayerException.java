package org.storm.maps.exception;

public class MapLayerException extends RuntimeException {

  public MapLayerException(String msg) {
    super(msg);
  }

  public MapLayerException(String msg, Throwable th) {
    super(msg, th);
  }

}
