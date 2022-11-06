package org.storm.sound.types;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.slf4j.Slf4j;
import org.storm.sound.Sound;

import java.nio.file.Paths;

/**
 * A Sound implementation which uses a Javax MediaPlayer
 */
@Slf4j
public class MediaSound implements Sound {

  private final MediaPlayer sound;

  public MediaSound(String path, boolean loop) {
    this.sound = new MediaPlayer(new Media(Paths.get(path).toUri().toString()));
    this.sound.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
    this.sound.setOnError(() -> log.error("error occurred with background music"));
  }

  public MediaSound(String path, int loopCount) {
    this(path, false);
    this.sound.setCycleCount(loopCount);
  }

  @Override
  public void play() {
    this.sound.play();
  }

  @Override
  public void pause() {
    this.sound.pause();
  }

  @Override
  public void stop() {
    this.sound.stop();
  }

  @Override
  public void adjustVolume(double volume) {
    this.sound.setVolume(volume);
  }

}
