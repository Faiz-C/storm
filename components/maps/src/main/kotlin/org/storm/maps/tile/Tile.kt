package org.storm.maps.tile

import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.math.geometry.shapes.AxisAlignedRectangle

class Tile(x: Double, y: Double, width: Double, height: Double) : ImmovableEntity(AxisAlignedRectangle(x, y, width, height))
