package org.storm.maps.layer

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import org.storm.core.context.Context
import org.storm.core.context.RESOLUTION
import org.storm.core.utils.ImageUtils.crop
import org.storm.maps.exception.MapLayerException
import org.storm.physics.entity.Entity
import java.io.FileInputStream

class BackgroundLayer(
    private val background: Image
) : Layer(false) {

    constructor(imagePath: String) : this(Image(FileInputStream(imagePath)))

    override fun addEntity(entity: Entity) {
        throw MapLayerException("cannot add entities to the background layer")
    }

    override fun removeEntity(entity: Entity) {
        throw MapLayerException("cannot remove entities to the background layer")
    }

    override suspend fun render(gc: GraphicsContext, x: Double, y: Double) {
        val resolution = Context.RESOLUTION
        val croppedBackground = crop(
            background, x.toInt(), y.toInt(), (x + resolution.width).toInt(), (y + resolution.height)
                .toInt()
        )
        gc.drawImage(croppedBackground, 0.0, 0.0)
    }

    override suspend fun update(time: Double, elapsedTime: Double) {
        // Default behaviour involves no updating but something like Parallax might involve the need to update
    }
}
