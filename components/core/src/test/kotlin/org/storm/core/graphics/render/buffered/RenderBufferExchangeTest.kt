package org.storm.core.graphics.render.buffered

import org.junit.jupiter.api.Test
import org.storm.core.graphics.Image
import org.storm.core.graphics.geometry.Point
import org.storm.core.graphics.geometry.shape.Rectangle

class RenderBufferExchangeTest {

    private val bufferExchange = RenderBufferExchange()
    private val context = BufferedRenderContext()

    private val fakeImage = object: Image {
        override val uri: String = ""
        override val width: Double = 0.0
        override val height: Double = 0.0

        override fun crop(
            x: Double,
            y: Double,
            width: Double,
            height: Double
        ): Image {
            return this
        }

        override fun chop(
            width: Double,
            height: Double
        ): Array<Array<Image>> {
            return arrayOf()
        }
    }

    @Test
    fun `drawing primitives give the expected commands`() {
        assert(this.bufferExchange.renderingBuffer() == null) {
            "Nothing should be set to render before the first usage"
        }

        val polygonPoints = listOf(Point(0.0, 0.0), Point(5.0, 5.0), Point(5.0, 0.0))

        this.bufferExchange.write {
            this.context.withBuffer(it) {
                this.drawLine(0.0, 0.0, 2.0, 2.0)
                this.drawText("hello", 5.0, 1.0)
                this.drawRect(0.0, 0.0, 1.0, 1.0)
                this.drawEllipse(5.0, 1.0, 5.0, 3.0)
                this.drawPolygon(polygonPoints)
                this.drawImage(fakeImage, 1.0, 3.0)
            }
        }

        val expectedCommands = listOf(
            RenderCommand.Line(0.0, 0.0, 2.0, 2.0),
            RenderCommand.Text("hello", 5.0, 1.0),
            RenderCommand.Rect(0.0, 0.0, 1.0, 1.0),
            RenderCommand.Ellipse(5.0, 1.0, 5.0, 3.0),
            RenderCommand.Polygon(polygonPoints),
            RenderCommand.Image(fakeImage, 1.0, 3.0)
        )

        val renderBuffer = this.bufferExchange.renderingBuffer()

        assert(renderBuffer != null)

        for (i in expectedCommands.indices) {
            assert(renderBuffer!!.commands[i] == expectedCommands[i]) {
                "Expected index $i in both renderBuffer and expectedCommand to be the same"
            }
        }
    }

    @Test
    fun `state changes are commands as well`() {
        assert(this.bufferExchange.renderingBuffer() == null) {
            "Nothing should be set to render before the first usage"
        }

        val boundary = Rectangle(0.0, 0.0, 1.0, 1.0)

        this.bufferExchange.write {
            this.context.withBuffer(it) {
                this.withSettings(thickness = 1.0) {
                    this.withClip(boundary) {
                        this.drawLine(0.0, 0.0, 2.0, 2.0)
                    }
                }
            }
        }

        val expectedCommands = listOf(
            RenderCommand.ChangeSettings(this.context.settings.copy(thickness = 1.0)),
            RenderCommand.Save,
            RenderCommand.Clip(boundary),
            RenderCommand.Line(0.0, 0.0, 2.0, 2.0),
            RenderCommand.Restore,
            RenderCommand.ChangeSettings(this.context.settings)
        )

        val renderBuffer = this.bufferExchange.renderingBuffer()

        assert(renderBuffer != null)

        for (i in expectedCommands.indices) {
            assert(renderBuffer!!.commands[i] == expectedCommands[i]) {
                "Expected index $i in both renderBuffer and expectedCommand to be the same"
            }
        }
    }

    @Test
    fun `submitting multiple buffers replaces the buffer for rendering`() {
        assert(this.bufferExchange.renderingBuffer() == null) {
            "Nothing should be set to render before the first usage"
        }

        this.bufferExchange.write {
            this.context.withBuffer(it) {
                this.drawText("hello", 0.0, 0.0)
            }
        }

        this.bufferExchange.write {
            this.context.withBuffer(it) {
                this.drawLine(0.0, 0.0, 1.0, 1.0)
            }
        }

        val renderBuffer = this.bufferExchange.renderingBuffer()
        assert(renderBuffer != null)
        assert(renderBuffer!!.commands.size == 1)
        assert(renderBuffer.commands.contains(RenderCommand.Line(0.0, 0.0, 1.0, 1.0)))
    }

    @Test
    fun `exchange tracks the last buffer for rendering`() {
        assert(this.bufferExchange.renderingBuffer() == null) {
            "Nothing should be set to render before the first usage"
        }

        this.bufferExchange.write {
            this.context.withBuffer(it) {
                this.drawText("hello", 0.0, 0.0)
            }
        }

        val renderBuffer = this.bufferExchange.renderingBuffer()
        assert(renderBuffer != null)

        // Ask for the buffer again before a new one is submitted, we should get back the old buffer from above
        val lastBuffer = this.bufferExchange.renderingBuffer()
        assert(lastBuffer != null)
        assert(lastBuffer == renderBuffer)
    }

}