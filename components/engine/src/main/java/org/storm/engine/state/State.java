package org.storm.engine.state;

import javafx.scene.canvas.GraphicsContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.storm.core.render.Renderable;
import org.storm.core.update.Updatable;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.request.RequestQueueProcessor;
import org.storm.physics.entity.Entity;
import org.storm.physics.transforms.UnitConvertor;
import org.storm.sound.manager.SoundManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A State represents a contained state within a game which is capable of rendering, updating and processing user inputs.
 * This could be a menu, a level, etc...
 */
@Getter
@AllArgsConstructor
public abstract class State implements Renderable, Updatable, RequestQueueProcessor {

  protected final List<Updatable> updatables;

  protected final List<Renderable> renderables;

  protected final List<Entity> entities;

  protected final SoundManager soundManager;

  @Getter(AccessLevel.NONE)
  protected final UnitConvertor unitConvertor;

  protected State(SoundManager soundManager, UnitConvertor unitConvertor) {
    this.updatables = new ArrayList<>();
    this.renderables = new ArrayList<>();
    this.entities = new ArrayList<>();
    this.soundManager = soundManager;
    this.unitConvertor = unitConvertor;
  }

  protected State(UnitConvertor unitConvertor) {
    this(new SoundManager(), unitConvertor);
  }

  protected State() {
    this(new SoundManager(), new UnitConvertor() {});
  }

  @Override
  public void render(GraphicsContext graphicsContext, double x, double y) {
    this.entities.forEach(entity -> entity.transform(this.unitConvertor).render(graphicsContext, x, y));
    this.renderables.forEach(renderable -> renderable.render(graphicsContext, x, y));
  }

  @Override
  public void update(double time, double elapsedTime) {
    this.updatables.forEach(updatable -> updatable.update(time, elapsedTime));
  }

  /**
   * This method is called when the State is first added to the StormEngine. It is only called once.
   */
  public void preload(RequestQueue requestQueue) {
    // By default, a State doesn't preload anything
  }

  /**
   * This method is called every time the State is swapped too by the StormEngine.
   */
  public void load(RequestQueue requestQueue) {
    // By default, a State doesn't load anything
  }

  /**
   * This method is called every time the State is swapped off of by the StormEngine.
   */
  public void unload(RequestQueue requestQueue) {
    // By default, a State doesn't unload anything
  }

  /**
   * This method is called to reset the State to its POST preloaded state when being swapped too by the StormEngine.
   */
  public void reset(RequestQueue requestQueue) {
    // By default, a State doesn't need to reset anything
  }

}
