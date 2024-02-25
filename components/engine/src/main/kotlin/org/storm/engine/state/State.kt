package org.storm.engine.state

import javafx.scene.canvas.GraphicsContext
import org.storm.core.render.Renderable
import org.storm.core.update.Updatable
import org.storm.engine.request.RequestQueue
import org.storm.engine.request.StormEngineProcessor
import org.storm.physics.entity.Entity
import org.storm.physics.transforms.UnitConvertor
import org.storm.sound.manager.SoundManager

/**
 * A State represents a contained state within a game which is capable of rendering, updating and processing user inputs.
 * This could be a menu, a level, etc...
 */
abstract class State protected constructor(
    protected val soundManager: SoundManager = SoundManager(),
    protected val unitConvertor: UnitConvertor = object : UnitConvertor {}
) : Renderable, Updatable, StormEngineProcessor {

    // Expose as immutable
    val entities: Set<Entity> get() = this.mutableEntities
    val updatables: Set<Updatable> get() = this.mutableUpdatables
    val renderables: Set<Renderable> get() = this.mutableRenderables

    protected val mutableEntities: MutableSet<Entity> = mutableSetOf()
    protected val mutableUpdatables: MutableSet<Updatable> = mutableSetOf()
    protected val mutableRenderables: MutableSet<Renderable> = mutableSetOf()

    override suspend fun render(gc: GraphicsContext, x: Double, y: Double) {
        this.entities.forEach { entity -> entity.transform(this.unitConvertor).render(gc, x, y) }
        this.renderables.forEach { renderable -> renderable.render(gc, x, y) }
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        this.updatables.forEach { updatable -> updatable.update(time, elapsedTime) }
    }

    /**
     * This method is called when the State is first added to the StormEngine. It is only called once.
     * @param requestQueue a RequestQueue to submit alteration requests to the StormEngine
     */
    open fun preload(requestQueue: RequestQueue) {
        // By default, a State doesn't preload anything
    }

    /**
     * This method is called every time the State is swapped too by the StormEngine.
     * @param requestQueue a RequestQueue to submit alteration requests to the StormEngine
     */
    open fun load(requestQueue: RequestQueue) {
        // By default, a State doesn't load anything
    }

    /**
     * This method is called every time the State is swapped off of by the StormEngine.
     * @param requestQueue a RequestQueue to submit alteration requests to the StormEngine
     */
    open fun unload(requestQueue: RequestQueue) {
        // By default, a State doesn't unload anything
    }

    /**
     * This method is called to reset the State to its POST preloaded state when being swapped too by the StormEngine.
     * @param requestQueue a RequestQueue to submit alteration requests to the StormEngine
     */
    fun reset(requestQueue: RequestQueue) {
        // By default, a State doesn't need to reset anything
    }
}
