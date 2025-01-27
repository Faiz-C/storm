package org.storm.core.context

import org.junit.jupiter.api.Test
import org.storm.core.ui.Resolution

class CoreContextTest {

    @Test
    fun testResolutionSettings() {
        val defaultResolution = Context.RESOLUTION
        assert(defaultResolution == Resolution.SD) {
            "Expected default Resolution to be SD (640 x 480)"
        }

        Context.setResolution(Resolution.UHD)
        assert(Context.RESOLUTION == Resolution.UHD) {
            "Expected resolution to update to 4K (3840 x 2160)"
        }
    }

    @Test
    fun testUnitConvertorSettings() {
        val defaultUnitConvertor = Context.UNIT_CONVERTOR
        assert(defaultUnitConvertor.ppu == 10.0) {
            "Expected default pixels per unit to be 10.0"
        }

        Context.setPPU(100.0)
        assert(Context.UNIT_CONVERTOR.ppu == 10.0) {
            "Expected unit convertor to not change until scheduled updates are run"
        }

        Context.runScheduledUpdates()
        assert(Context.UNIT_CONVERTOR.ppu == 100.0) {
            "Expected unit convertor to now have pixels per unit be 100.0"
        }
    }
}