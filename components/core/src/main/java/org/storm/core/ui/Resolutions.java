package org.storm.core.ui;

import java.awt.*;

/**
 * A collection of common resolutions
 */
public class Resolutions {

  // Common Resolutions
  public static final Resolution SD = new Resolution(640, 480);
  public static final Resolution HD = new Resolution(1280, 720);
  public static final Resolution FHD = new Resolution(1920, 1080);
  public static final Resolution QHD = new Resolution(2560, 1440);
  public static final Resolution UHD = new Resolution(3840, 2160);
  public static final Resolution FULL_SCREEN = new Resolution(
    Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
    Toolkit.getDefaultToolkit().getScreenSize().getHeight()
  );


  private Resolutions() {}

}
