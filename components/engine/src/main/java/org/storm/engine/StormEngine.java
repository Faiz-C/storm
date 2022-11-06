package org.storm.engine;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.FastMath;
import org.storm.core.input.Translator;
import org.storm.core.input.action.ActionManager;
import org.storm.core.ui.Resolution;
import org.storm.core.ui.Window;
import org.storm.engine.exception.StormEngineException;
import org.storm.engine.request.RequestQueue;
import org.storm.engine.state.State;
import org.storm.physics.PhysicsEngine;
import org.storm.physics.transforms.UnitConvertor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The StormEngine combines the multiple components of the Storm libraries to create a simple, straight forward and
 * efficient 2D game engine.
 */
public class StormEngine {

  // Components
  @Getter
  private final PhysicsEngine physicsEngine;

  private final ActionManager actionManager;

  @Getter
  private final Window window;

  // Game Loop Vars
  private double minRenderDelay;
  private double minLogicDelay;
  private double accumulatedRenderDelay;
  private double accumulatedLogicDelay;
  private double accumulator;
  private double fixedTimeStepInterval;
  private double lastUpdateTime;
  private Timeline timeline;

  // Stateful vars
  private State currentState;

  private final Map<String, State> states;

  private boolean running;

  @Getter
  @Setter
  private boolean fpsChangeAllow;

  private final RequestQueue requestQueue;

  private final UnitConvertor unitConvertor;

  public StormEngine(PhysicsEngine physicsEngine, ActionManager actionManager, UnitConvertor unitConvertor,
                     Resolution resolution, int inputQueueMax, int targetLogicFps, int targetRenderFps) {
    this.physicsEngine = physicsEngine;
    this.actionManager = actionManager;
    this.window = new Window(resolution);
    this.fpsChangeAllow = true;
    this.setTargetFps(targetLogicFps, targetRenderFps);
    this.fpsChangeAllow = false;
    this.accumulator = 0.0;
    this.states = new HashMap<>();
    this.requestQueue = new RequestQueue(inputQueueMax);
    this.unitConvertor = unitConvertor;

    this.physicsEngine.setPaused(true);
  }

  public StormEngine(PhysicsEngine physicsEngine, ActionManager actionManager, UnitConvertor unitConvertor,
                     Resolution resolution, int inputQueueMax, int targetFps) {
    this(physicsEngine, actionManager, unitConvertor, resolution, inputQueueMax, targetFps, targetFps);
  }

  /**
   * Changes the logic and render fps to the given if allowed
   *
   * @param targetLogicFps  new logic fps to use
   * @param targetRenderFps new render fps to use
   */
  public void setTargetFps(int targetLogicFps, int targetRenderFps) {
    if (!this.fpsChangeAllow) throw new StormEngineException("fps change not allowed at this time");

    if (this.timeline != null) this.timeline.stop();

    // Game loop vars
    double targetFps = FastMath.max(targetLogicFps, targetRenderFps);
    this.minRenderDelay = 1000000000.0 / targetRenderFps;
    this.minLogicDelay = 1000000000.0 / targetLogicFps;
    this.accumulatedRenderDelay = this.minRenderDelay; // To allow first run render
    this.accumulatedLogicDelay = this.minLogicDelay; // To allow first run logic updates
    this.fixedTimeStepInterval = 1000000000.0 / targetFps;
    this.timeline = this.createTimeline(targetFps, this::run);
  }

  /**
   * Changes the logic and render fps to the given if allowed
   *
   * @param targetFps new fps to use for both logic and rendering
   */
  public void setTargetFps(int targetFps) {
    this.setTargetFps(targetFps, targetFps);
  }

  /**
   * Adds the given State under the given unique id. This will also initialize the state
   *
   * @param stateId unique id to identify the state by
   * @param state   State to add
   */
  public void addState(String stateId, State state) {
    state.preload(this.requestQueue);
    this.states.put(stateId, state);
  }

  /**
   * Removes the State associated with the given id
   *
   * @param stateId unique id of the State to remove
   */
  public void removeState(String stateId) {
    this.states.remove(stateId);
  }

  /**
   * Swaps the current active State to the one associated with the given id with or without resetting it.
   *
   * @param stateId unique id of the state to switch to
   * @param reset   true if resetting the state is wanted, false otherwise
   */
  public void swapState(String stateId, boolean reset) {
    if (!this.states.containsKey(stateId)) throw new StormEngineException("could not find state for id " + stateId);
    if (this.states.get(stateId) == this.currentState) return;

    this.timeline.stop();

    if (this.currentState != null) this.currentState.unload(this.requestQueue);

    this.currentState = this.states.get(stateId);

    this.physicsEngine.clearAllForces();
    this.physicsEngine.setEntities(this.currentState.getEntities());

    if (reset) this.currentState.reset(this.requestQueue);

    this.currentState.load(this.requestQueue);
    this.timeline.play();
  }

  /**
   * Swaps the current active State to the one associated with the given id without resetting
   *
   * @param stateId unique id of the state to switch to
   */
  public void swapState(String stateId) {
    this.swapState(stateId, false);
  }

  /**
   * Registers the given translator to translate KeyEvents to String actions for the following events:
   * key pressed, key released
   *
   * @param inputTranslator Translator to use
   */
  public void addKeyRegister(Translator<KeyEvent, String> inputTranslator) {
    this.window.setOnKeyPressed(keyEvent -> this.actionManager.startUsing(inputTranslator.translate(keyEvent)));
    this.window.setOnKeyReleased(keyEvent -> this.actionManager.stopUsing(inputTranslator.translate(keyEvent)));
  }

  /**
   * Registers the given translator to translate MouseEvents to String actions for the following events:
   * mouse pressed, mouse released, mouse entered, mouse exited
   *
   * @param inputTranslator Translator to use
   */
  public void addMouseRegister(Translator<MouseEvent, String> inputTranslator) {
    this.window.setOnMousePressed(mouseEvent -> this.actionManager.startUsing(inputTranslator.translate(mouseEvent)));
    this.window.setOnMouseReleased(mouseEvent -> this.actionManager.stopUsing(inputTranslator.translate(mouseEvent)));
    this.window.setOnMouseEntered(mouseEvent -> this.actionManager.startUsing(inputTranslator.translate(mouseEvent)));
    this.window.setOnMouseExited(mouseEvent -> this.actionManager.stopUsing(inputTranslator.translate(mouseEvent)));
  }

  /**
   * Runs the StormEngine
   */
  public void run() {
    if (this.running) throw new StormEngineException("storm engine is already running");

    this.lastUpdateTime = System.nanoTime();
    this.running = true;
    this.timeline.play();
  }

  /**
   * Internal run method which represents the game loop and actually runs the engine
   *
   * @param event Javafx Event -- unused
   */
  private void run(Event event) {
    if (!this.running || this.currentState == null) return;

    double now = System.nanoTime();
    double elapsedFrameTime = now - this.lastUpdateTime;
    this.lastUpdateTime = now;
    this.accumulator += elapsedFrameTime;

    // First handle a set of requests
    this.requestQueue
      .next()
      .ifPresent(requests -> requests
        .forEach(request -> request.execute(this)));

    // Update accumulated delays
    this.accumulatedRenderDelay += elapsedFrameTime;
    this.accumulatedLogicDelay += elapsedFrameTime;

    if (this.accumulatedLogicDelay >= this.minLogicDelay) {
      // Process Input
      this.currentState.process(this.actionManager.getReadonly(), this.requestQueue);

      // Then allow the state to do any internal updating
      this.currentState.update(toSeconds(this.lastUpdateTime), toSeconds(elapsedFrameTime));

      // Apply Physics
      while (accumulator >= this.fixedTimeStepInterval) {
        this.physicsEngine.update(toSeconds(this.lastUpdateTime), toSeconds(elapsedFrameTime));
        this.accumulator -= this.fixedTimeStepInterval;
        this.lastUpdateTime += this.fixedTimeStepInterval;
      }

      this.accumulatedLogicDelay = 0;
    }

    if (++this.accumulatedRenderDelay >= this.minRenderDelay) {
      // Render the state
      this.window.clear();
      this.currentState.render(this.window.getGraphicsContext(), 0, 0);

      this.accumulatedRenderDelay = 0;
    }

  }

  /**
   * Creates a timeline which triggers at the given fps and uses the given timelineHandler
   *
   * @param fps fps to run the timeline at
   * @param timelineHandler handler to call on each trigger of the timeline
   * @return a Timeline which triggers based on the given fps, calls the given handler when triggered and loops indefinitely
   */
  private Timeline createTimeline(double fps, Consumer<Event> timelineHandler) {
    Timeline tl = new Timeline(fps, new KeyFrame(Duration.millis(1000.0 / fps), timelineHandler::accept));
    tl.setAutoReverse(false);
    tl.setCycleCount(Animation.INDEFINITE);
    return tl;
  }

  /**
   * @param nanoSeconds time (in nanoseconds) to convert to seconds
   * @return the given nanoseconds in seconds
   */
  private static double toSeconds(double nanoSeconds) {
    return nanoSeconds / 1000000000;
  }

}
