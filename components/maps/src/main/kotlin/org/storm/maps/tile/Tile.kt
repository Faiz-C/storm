package org.storm.maps.tile

import org.storm.physics.entity.ImmovableEntity
import org.storm.physics.math.geometry.shapes.AABB

class Tile(x: Double, y: Double, width: Double, height: Double) : ImmovableEntity(AABB(x, y, width, height))
