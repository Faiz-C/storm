package org.storm.physics.context

import org.storm.core.context.Context
import org.storm.physics.transforms.UnitConvertor

object PhysicsContext {
    const val PPU = "ppu" // Pixels Per Unit
    const val UNIT_CONVERTOR = "unitConvertor"
}

/**
 * @return The number of pixels per unit used for physics calculations. This is used to convert between pixels and units.
 */
val Context.PPU get() = SETTINGS[PhysicsContext.PPU] as? Double ?: 10.0

/**
 * @return The unit convertor used to convert between pixels and units.
 */
val Context.UNIT_CONVERTOR: UnitConvertor get() = SETTINGS[PhysicsContext.UNIT_CONVERTOR] as? UnitConvertor
    ?: standardUnitConvertor

/**
 * Sets the number of pixels per unit used for physics calculations. This is used to convert between pixels and units.
 *
 * @param ppu the number of pixels per unit
 * @param schedule whether to schedule an update or apply it immediately (default false)
 */
fun Context.setPPU(ppu: Double, schedule: Boolean = false) {
    update(schedule) { settings ->
        settings + mapOf(
            PhysicsContext.PPU to ppu,
            PhysicsContext.UNIT_CONVERTOR to createUnitConvertor(ppu)
        )
    }
}

private val standardUnitConvertor = createUnitConvertor(Context.PPU)

private fun createUnitConvertor(ppu: Double): UnitConvertor = object: UnitConvertor {
    override fun toUnits(pixels: Double) = pixels / ppu
    override fun toPixels(units: Double) = units * ppu
}

