package org.storm.engine.state;

import javafx.scene.canvas.GraphicsContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.storm.core.input.Processor;
import org.storm.core.render.Renderable;
import org.storm.core.update.Updatable;
import org.storm.engine.request.RequestQueue;
import org.storm.physics.entity.Entity;
import org.storm.sound.manager.SoundManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A State represents a contained state within a game which is capable of rendering, updating and processing user inputs.
 * This could be a menu, a level, etc...
 */
@Getter
@AllArgsConstructor
public abstract class State implements Renderable, Updatable, Processor {

  protected final List<Updatable> updatables;

  protected final List<Renderable> renderables;

  protected final List<Entity> entities;

  protected final SoundManager soundManager;

  protected final RequestQueue requestQueue;

  protected State(SoundManager soundManager, int requestSizeLimit) {
    this.updatables = new ArrayList<>();
    this.renderables = new ArrayList<>();
    this.entities = new ArrayList<>();
    this.soundManager = soundManager;
    this.requestQueue = new RequestQueue(requestSizeLimit);
  }

  protected State(int requestSizeLimit) {
    this(new SoundManager(), requestSizeLimit);
  }

  protected State() {
    this(10);
  }

  @Override
  public void render(GraphicsContext graphicsContext, double x, double y) {
    this.entities.forEach(entity -> entity.render(graphicsContext, x, y));
    this.renderables.forEach(renderable -> renderable.render(graphicsContext, x, y));
  }

  @Override
  public void update(double time, double elapsedTime) {
    this.entities.forEach(entity -> entity.update(time, elapsedTime));
    this.updatables.forEach(updatable -> updatable.update(time, elapsedTime));
  }

  /**
   * Initializes anything the state needs to function normally
   */
  public abstract void init();

  /**
   * Loads any aspects the state may need when it is switched too
   */
  public abstract void load();

  /**
   * Unloads any aspects the state may need to when it is switched off of
   */
  public abstract void unload();

}
