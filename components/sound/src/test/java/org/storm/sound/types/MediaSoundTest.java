package org.storm.sound.types;

import javafx.application.Application;
import javafx.stage.Stage;
import org.storm.sound.Sound;

public class MediaSoundTest extends Application {

  private static final String BGM_FILE = "src/test/resources/bgm.mp3";

  private Sound bgm;

  @Override
  public void start(Stage stage) {
    bgm = new MediaSound(BGM_FILE, false);
    bgm.adjustVolume(0.1);
    bgm.play();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
