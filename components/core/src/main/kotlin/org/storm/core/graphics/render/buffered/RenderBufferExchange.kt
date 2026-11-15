package org.storm.core.graphics.render.buffered

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

    // Tracks the last buffer that the rendering thread was working on. This is important to have as a fallback
    // in case the game logic thread doesn't produce a new buffer before we need to render again.
    private var lastBuffer: RenderBuffer? = null

    /**
     * Attempts to get a buffer from the pool of available buffers, uses [writer] to fill that buffer, then
     * submits it for rendering. This method offers a simple pattern for the writing have of the exchange using
     * the existing methods:
     *
     * ```
     * val buffer = this.writeBuffer()
     * writer(buffer)
     * this.submit(buffer)
     * ```
     * @param writer a block of code to run which will write to the next available buffer
     */
    fun write(writer: (RenderBuffer) -> Unit) {
        // Acquire a buffer for writing
        val buffer = this.writeBuffer()

        // Write commands to the buffer
        writer(buffer)

        // Submit the buffer to render next
        this.submit(buffer)
    }

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
     * Submits [renderBuffer] to be the next buffer to offer for rendering. If another buffer is already waiting
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
        // Now we have a couple of cases to consider:
        // 1) `new` is null, which means there is no new buffer to render. In this case we want to return the last buffer
        //    we offered for rendering. This can be null in the case nothing was done yet.
        // 2) `new` is not null, which means we need to offer this buffer but also, if `lastBuffer` is holding a value,
        // then it needs to be cleared and the buffer returned to the pool. Then `lastBuffer` must be set to `new`.

        // we set to null here to "consume" the next and properly state we have no next
        val new = this.next.getAndSet(null) ?: return this.lastBuffer

        if (this.lastBuffer != null) {
            this.lastBuffer!!.clear()
            this.pool.offer(this.lastBuffer!!)
        }

        this.lastBuffer = new

        return this.lastBuffer
    }

}