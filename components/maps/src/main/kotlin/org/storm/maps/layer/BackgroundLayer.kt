package org.storm.maps.layer

import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.render.canvas.Canvas
import org.storm.core.render.Image
import org.storm.core.render.impl.JfxImage
import org.storm.maps.exception.MapLayerException
import org.storm.physics.entity.Entity

class BackgroundLayer(
    private val background: Image
) : Layer(false) {

    constructor(imagePath: String) : this(JfxImage(imagePath))

    override fun addEntity(entity: Entity) {
        throw MapLayerException("cannot add entities to the background layer")
    }

    override fun removeEntity(entity: Entity) {
        throw MapLayerException("cannot remove entities to the background layer")
    }

    override suspend fun render(canvas: Canvas, x: Double, y: Double) {
        val resolution = Context.RESOLUTION
        val croppedBackground = background.crop(x, y, x + resolution.width, y + resolution.height)
        canvas.drawImageWithPixels(croppedBackground, 0.0, 0.0)
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        // Default behaviour involves no updating but something like Parallax might involve the need to update
    }
}
