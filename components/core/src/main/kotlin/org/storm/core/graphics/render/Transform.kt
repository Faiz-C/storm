package org.storm.core.graphics.render

/**
 * A Transform holds visual/spatial data for a game object. Its coordinates represent the *center* of the transform.
 * All spatial and scaling data is stored in *units* instead of pixels. Rotation is in degrees.
 */
data class Transform(
    var x: Double,
    var y: Double,
    var px: Double = x,
    var py: Double = y,
    var width: Double = 0.0,
    var height: Double = 0.0,
    var xScale: Double = 1.0,
    var yScale: Double = 1.0,
    var rotation: Double, // The rotation of the transform in degrees
    val layer: Int = 0 // Which layer to render on
) {

    /**
     * The scaled width of this transform.
     */
    val visualWidth: Double get() = this.width * this.xScale

    /**
     * The scaled height of this transform.
     */
    val visualHeight: Double get() = this.height * this.yScale

    /**
     * The x coordinate of the top left corner of the transform.
     */
    val topLeftX: Double get() = this.x - this.visualWidth / 2

    /**
     * The y coordinate of the top left corner of the transform.
     */
    val topLeftY: Double get() = this.y - this.visualHeight / 2

    /**
     * Saves the current position of the transform.
     */
    fun save() {
        this.px = this.x
        this.py = this.y
    }

    /**
     * Restores the position of the transform from the last save point.
     */
    fun restore() {
        this.x = this.px
        this.y = this.py
    }

    /**
     * Translates the transform by [dx], [dy].
     *
     * @param dx the x delta to translate by
     * @param dy the y delta to translate by
     */
    fun translate(dx: Double, dy: Double) {
        this.x += dx
        this.y += dy
    }

    /**
     * Immediately moves the transform to the given [x], [y]
     *
     * @param x the x position to move to
     * @param y the y position to move to
     */
    fun snapTo(x: Double, y: Double) {
        this.translate(x - this.x, y - this.y)
        this.save()
    }

    /**
     * Interpolates the transforms position by the given alpha and returns both calculated x and y coordinates.
     *
     * @param alpha the alpha value to interpolate by
     * @return the pair of interpolated (x, y) coordinates
     */
    fun interpolate(alpha: Double): Pair<Double, Double> {
        val ix = this.px + (this.x - this.px) * alpha
        val iy = this.py + (this.y - this.py) * alpha
        return ix to iy
    }

}
