package org.storm.animations.sprite;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Getter;
import org.storm.animations.Animation;

/**
 * A specific type of Animation focusing on the use of 2D Sprites and SpriteSheets.
 */
public class SpriteAnimation implements Animation {

  private final Image[] sprites;

  private final int loopCount;

  private final int frameDelay;

  private int currentFrame;

  private int currentLoop;

  private int currentSprite;

  @Getter
  private boolean playing;

  public SpriteAnimation(Image[] sprites, int frameDelay, int loopCount) {
    if (loopCount != LOOP_INDEFINITELY && loopCount < 0) {
      throw new IllegalArgumentException("loopCount must be >= 0");
    }

    this.sprites = sprites;
    this.frameDelay = frameDelay;
    this.loopCount = loopCount;
    this.playing = true;
  }

  @Override
  public void play() {
    this.playing = true;
  }

  @Override
  public void pause() {
    this.playing = false;
  }

  public void reset() {
    this.currentFrame = 0;
    this.currentSprite = 0;
    this.currentLoop = 0;
  }

  @Override
  public void render(GraphicsContext graphicsContext, double x, double y) {
    graphicsContext.drawImage(this.sprites[this.currentSprite], x, y);
  }

  @Override
  public void update(double time, double elapsedTime) {

    if (!this.playing || this.currentLoop == this.loopCount) return;

    this.currentFrame++;

    if (this.currentFrame == this.frameDelay) {
      this.currentFrame = 0;
      this.currentSprite++;
    }

    if (this.currentSprite == this.sprites.length) {
      this.currentSprite = 0;
      this.currentLoop = (this.currentLoop == LOOP_INDEFINITELY) ? LOOP_INDEFINITELY : this.currentLoop + 1;
    }

  }

}
