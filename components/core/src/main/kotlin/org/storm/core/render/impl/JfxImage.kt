package org.storm.core.render.impl

import javafx.scene.image.WritableImage
import org.storm.core.render.Image
import java.io.FileInputStream

class JfxImage(val image: javafx.scene.image.Image): Image {

    constructor(path: String): this(javafx.scene.image.Image(FileInputStream(path)))

    override val uri: String? = image.url

    override val width: Double = image.width

    override val height: Double = image.height

    override fun crop(
        x: Double,
        y: Double,
        width: Double,
        height: Double
    ): Image {
        return JfxImage(WritableImage(image.pixelReader, x.toInt(), y.toInt(), width.toInt(), height.toInt()))
    }

    override fun chop(width: Double, height: Double): Array<Array<Image>> {
        return Array((image.height / height).toInt()) { r ->
            val colCount = (image.width / width).toInt()

            (0 until colCount).map { c ->
                crop(c * width, r * height, width, height)
            }.toTypedArray()
        }
    }

}