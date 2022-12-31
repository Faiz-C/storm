package org.storm.physics.entity

import org.storm.physics.math.geometry.shapes.Shape

class SimpleEntity(hurtBox: Shape, speed: Double, mass: Double, restitution: Double) : Entity(hurtBox, speed, mass, restitution)
