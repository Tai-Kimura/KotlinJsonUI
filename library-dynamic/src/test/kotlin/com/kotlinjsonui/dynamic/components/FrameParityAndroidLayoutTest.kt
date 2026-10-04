package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Layout boxes the frame-parity inventory (2026-10-05, Android x web) found
 * away from their declaration, decided the same way as the kjui codegen
 * (jsonui-cli kjui_tools spec frame_parity_android_layout_spec.rb).
 */
class FrameParityAndroidLayoutTest {

    private fun json(s: String) = JsonParser.parseString(s).asJsonObject

    // kjui-labelled-switch-fills-the-parent-width-under-wrapcontent: a weighted
    // label makes Compose's Row take its whole max width.
    @Test
    fun aLabelledSwitchUnderWrapContentGivesItsLabelNoWeight() {
        assertFalse(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L","width":"wrapContent"}""")))
        assertFalse(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L"}""")))
    }

    @Test
    fun aLabelledSwitchWithADecidedWidthWeightsItsLabel() {
        assertTrue(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L","width":300}""")))
        assertTrue(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L","width":"matchParent"}""")))
        assertTrue(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L","weight":1}""")))
    }

    // kjui-scrollview-center-anchor-is-off-by-4: scrollBy reports
    // `delta − leftover` in float32. Near 1e9 floats are 64 apart, so a 1200 px
    // extent came back as 1216 and the centre started 8 px (4 dp) short.
    @Test
    fun theScrollToEndDeltaReportsTheExtentExactly() {
        val extent = 1200f
        val delta = DynamicScrollViewComponent.SCROLL_TO_END_DELTA
        assertEquals(extent, delta - (delta - extent), 0f)
        // The delta it replaced, as the control: 16 px off for this extent.
        assertEquals(1216f, 1e9f - (1e9f - extent), 0f)
    }
}
