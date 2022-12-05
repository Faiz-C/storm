package org.storm.animations;

import javafx.application.Application;
import javafx.stage.Stage;
import org.storm.animations.sprite.SpriteAnimation;
import org.storm.animations.sprite.SpriteSheet;
import org.storm.core.ui.Resolution;
import org.storm.core.ui.Window;

public class AnimationTest extends Application {

  private static final double BUFFER_WAIT_TIME = 10;
  private SpriteAnimation downSpriteAnimation;
  private SpriteAnimation rightSpriteAnimation;
  private SpriteAnimation upSpriteAnimation;
  private SpriteAnimation leftSpriteAnimation;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Window window = new Window(new Resolution(320, 240));

    SpriteSheet spriteSheet = new SpriteSheet("src/test/resources/spriteSheet.png", 32, 32);
    downSpriteAnimation = new SpriteAnimation(spriteSheet.getRow(0), 8, Animation.LOOP_INDEFINITELY);
    rightSpriteAnimation = new SpriteAnimation(spriteSheet.getRow(1), 8, 6);
    upSpriteAnimation = new SpriteAnimation(spriteSheet.getRow(2), 8, 10);
    leftSpriteAnimation = new SpriteAnimation(spriteSheet.getRow(3), 8, 20);

    // Loop logic just for testing
    Thread listeningThread = new Thread(() -> {
      double targetFps = 144.0;

      double fixedStepInterval = 1000000000.0 / targetFps; // 1/fps in nanoSeconds
      double time = System.nanoTime();
      double accumulator = 0.0;

      while (true) {
        double currentTime = System.nanoTime();
        double elapsedFrameTime = currentTime - time;
        time = currentTime;
        accumulator += elapsedFrameTime;

        while (accumulator >= fixedStepInterval) {
          updateAnimations();
          render(window);
          accumulator -= fixedStepInterval;
          time += fixedStepInterval;
        }

        double waitTime = toMilliSeconds(time - System.nanoTime() + fixedStepInterval);
        if (waitTime < 0) waitTime = BUFFER_WAIT_TIME; // we took too long to execute the loop, so wait for a fixed BUFFER WAIT TIME

        try {
          Thread.sleep((long) waitTime);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    listeningThread.setDaemon(true);
    listeningThread.start();

    primaryStage.setScene(window);
    primaryStage.show();

  }

  public static void main(String[] args) {
    launch(args);
  }

  private void render(Window window) {
    window.clear();
    downSpriteAnimation.render(window.getGraphicsContext(), 100, 100);
    rightSpriteAnimation.render(window.getGraphicsContext(), 100, 150);
    upSpriteAnimation.render(window.getGraphicsContext(), 150, 100);
    leftSpriteAnimation.render(window.getGraphicsContext(), 150, 150);
  }

  private void updateAnimations() {
    downSpriteAnimation.update(0, 0);
    rightSpriteAnimation.update(0, 0);
    upSpriteAnimation.update(0, 0);
    leftSpriteAnimation.update(0, 0);
  }

  private static double toMilliSeconds(double nanoSeconds) {
    return nanoSeconds / 1000000;
  }

}
