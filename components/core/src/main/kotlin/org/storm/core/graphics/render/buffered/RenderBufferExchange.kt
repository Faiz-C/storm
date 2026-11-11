package org.storm.core.graphics.render.buffered

import org.storm.core.graphics.render.RenderContext
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicReference

/**
 * Holds and manages [bufferPoolSize] number of [RenderBuffer] objects of size [bufferSize].
 */
class RenderBufferExchange(val bufferPoolSize: Int = 3, val bufferSize: Int = 256) {

    // Pool of fixed size RenderingBuffers. Using a queue makes this easy to manage.
    private val pool = ArrayBlockingQueue<RenderBuffer>(bufferPoolSize).also { queue ->
        repeat(bufferPoolSize) {
            queue.add(RenderBuffer(bufferSize))
        }
    }

    // Tracks the next buffer that should be used for rendering
    private val next: AtomicReference<RenderBuffer?> = AtomicReference(null)

    // Tracks the current buffer that the rendering thread is working on. This is important to have as a fallback
    // in case the game logic thread doesn't produce a new buffer before we need to render again.
    private var current: RenderBuffer? = null

    /**
     * Attempts to get a buffer from the pool of available buffers but if none are available we allow for dynamic
     * assignment to prevent blocking. It is rare this will happen so it's okay to dynamically assign here. Better than
     * blocking.
     *
     * @return Next available buffer to write to or a dynamically allocated buffer if none are available
     */
    fun writeBuffer(): RenderBuffer {
        return this.pool.poll() ?: RenderBuffer(bufferSize)
    }

    /**
     * Submits [renderBuffer] to be the next buffer that a [org.storm.core.graphics.render.Renderer] will work on. If another buffer is already waiting
     * it gets kicked in favour of [renderBuffer]. It is cleared and readded to the pool.
     *
     * @param renderBuffer [RenderBuffer] to set as the next buffer to render
     */
    fun submit(renderBuffer: RenderBuffer) {
        this.next.getAndSet(renderBuffer)?.let { existing ->
            existing.clear()
            this.pool.offer(existing)
        }
    }

    /**
     * Determines the next [RenderBuffer] to render, ensuring the [next] buffer is prioritized and old buffers are
     * recycled.
     *
     * @return The next [RenderBuffer] to render, null if there isn't one
     */
    fun renderingBuffer(): RenderBuffer? {
        // we set to null here to "consume" the next and properly state we have no next
        val new = this.next.getAndSet(null)

        // If a new buffer is ready we need to use that one. Clear and add the old buffer back into the pool for reuse.
        if (this.current != null && this.current != new) {
            this.current!!.clear()
            this.pool.offer(this.current!!)
            this.current = new
        }

        return this.current
    }

}