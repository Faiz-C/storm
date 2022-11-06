package org.storm.physics;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.util.Duration;
import lombok.Getter;
import org.storm.core.ui.Resolution;

public class Simulator {

  @Getter
  private final PhysicsEngine physicsEngine;

  private final Runnable renderHandler;

  private double lastUpdateTime;

  private final double targetFps;

  private final double fixedStepInterval;

  private double accumulator;

  public Simulator(Resolution resolution, double targetFps, Runnable renderHandler)  {
    this.physicsEngine = new ImpulseResolutionPhysicsEngine(resolution);
    this.targetFps = targetFps;
    this.fixedStepInterval = 1000000000.0 / this.targetFps;
    this.renderHandler = renderHandler;
    this.lastUpdateTime = System.nanoTime();

  }

  public void setPaused(boolean b) {
    this.physicsEngine.setPaused(b);
  }

  public boolean isPaused() {
    return this.physicsEngine.isPaused();
  }

  public void simulate() {
    KeyFrame loopFrame = new KeyFrame(Duration.millis(1000.0 / targetFps), this::run);
    Timeline timeline = new Timeline(targetFps, loopFrame);
    timeline.setAutoReverse(false);
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }

  private void run(Event e) {
    double now = System.nanoTime();
    double elapsedFrameTime = now - lastUpdateTime;
    lastUpdateTime = now;
    accumulator += elapsedFrameTime;

    while (accumulator >= fixedStepInterval) {
      physicsEngine.update(toSeconds(lastUpdateTime), toSeconds(elapsedFrameTime));
      accumulator -= fixedStepInterval;
      lastUpdateTime += fixedStepInterval;
    }

    this.renderHandler.run();
  }

  private static double toSeconds(double nanoSeconds) {
    return nanoSeconds * 0.000000001;
  }

  private static double toMilliSeconds(double nanoSeconds) {
    return nanoSeconds / 1000000;
  }
}
