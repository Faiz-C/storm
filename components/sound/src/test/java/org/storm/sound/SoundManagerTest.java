package org.storm.sound;

import javafx.application.Application;
import javafx.stage.Stage;
import org.storm.sound.manager.SoundManager;
import org.storm.sound.types.MediaSound;

public class SoundManagerTest extends Application {

  // Add your own sound files to test. Not adding my own for obvious reasons.
  private static final String BGM_FILE = "src/test/resources/bgm.mp3";
  private static final String BGM2_FILE = "src/test/resources/bgm2.mp3";
  private static final String SOUND_EFFECT_FILE = "src/test/resources/effect.mp3";

  private final SoundManager soundManager = new SoundManager();

  @Override
  public void start(Stage stage) {
    soundManager.add("effect", new MediaSound(SOUND_EFFECT_FILE, false));
    soundManager.add("bgm1", new MediaSound(BGM_FILE, true));
    soundManager.add("bgm2", new MediaSound(BGM2_FILE, false));

    soundManager.adjustVolume("effect", 0.5);
    soundManager.adjustVolume("bgm1", 0.1);
    soundManager.adjustVolume("bgm2", 0.3);

    soundManager.play("bgm1");
    soundManager.play("bgm2");
    soundManager.play("effect");
  }

  public static void main(String[] args) {
    launch(args);
  }

}
