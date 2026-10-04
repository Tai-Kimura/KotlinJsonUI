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
        assertTrue(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L","widthWeight":1}""")))
        assertFalse(DynamicSwitchComponent.labelFillsRow(json("""{"type":"Switch","label":"L","width":"wrap_content"}""")))
    }

    // kjui-dynamic-toggle-is-not-drawn-as-its-canonical-switch: the conformance
    // manifest has no fixture for an alias ("fixtures live on the canonical
    // section"), so codegen_parity never draws one, and a renderer of its own
    // goes unseen. Every declared alias must share its canonical section's arm
    // of DynamicView's dispatch. Read from the source: the arm is a `when`
    // branch, which no unit test can call without a composition.
    @Test
    fun everyDeclaredAliasIsDispatchedWithItsCanonicalSection() {
        val source = java.io.File("src/main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt").readText()
        val arms = Regex("""^\s*((?:"\w+",\s*)*"\w+")\s*->\s*(Dynamic\w+)\.create\(""", RegexOption.MULTILINE)
            .findAll(source)
            .flatMap { m -> Regex(""""(\w+)"""").findAll(m.groupValues[1]).map { it.groupValues[1] to m.groupValues[2] } }
            .toMap()
        assertTrue("dispatch arms read: ${arms.size}", arms.size > 20)
        com.kotlinjsonui.dynamic.generated.JsonUIComponentAliases.canonical.forEach { (alias, canonical) ->
            assertEquals("$alias (alias of $canonical)", arms[canonical], arms[alias])
        }
    }

    // kjui-oversized-child-is-centred-and-cut-to-its-parent: the container
    // hands a child with a numeric size where it places it, per axis; the
    // size stage anchors the declared box there (device arms:
    // OversizedChildPlacementTest). The codegen computes the same biases
    // (container_component.rb inject_overflow_bias!).
    @Test
    fun aBoxChildIsPlacedByItsOwnAlignmentElseTheContentAlignment() {
        assertEquals(-1f to -1f, DynamicContainerComponent.boxBias(null, androidx.compose.ui.Alignment.TopStart))
        assertEquals(0f to 0f, DynamicContainerComponent.boxBias(null, androidx.compose.ui.Alignment.Center))
        assertEquals(1f to 1f, DynamicContainerComponent.boxBias(null, androidx.compose.ui.Alignment.BottomEnd))
        assertEquals(1f to -1f, DynamicContainerComponent.boxBias(androidx.compose.ui.BiasAlignment(1f, -1f), androidx.compose.ui.Alignment.Center))
    }

    @Test
    fun aColumnOrRowChildTakesTheCrossAlignmentAndTheMainAxisGravity() {
        assertEquals(0f, DynamicContainerComponent.columnBias(null, androidx.compose.ui.Alignment.CenterHorizontally))
        assertEquals(1f, DynamicContainerComponent.columnBias(androidx.compose.ui.Alignment.End, androidx.compose.ui.Alignment.Start))
        assertEquals(1f, DynamicContainerComponent.rowBias(null, androidx.compose.ui.Alignment.Bottom))
        assertEquals(0f, DynamicContainerComponent.rowBias(androidx.compose.ui.Alignment.CenterVertically, androidx.compose.ui.Alignment.Top))
        assertEquals(-1f, DynamicContainerComponent.axisBias(start = false, end = false, center = false))
        assertEquals(1f, DynamicContainerComponent.axisBias(start = false, end = true, center = false))
        assertEquals(0f, DynamicContainerComponent.axisBias(start = false, end = false, center = true))
    }

    @Test
    fun onlyAChildWithANumericSizeIsGivenThePlacement() {
        val sized = DynamicContainerComponent.withOverflowBias(json("""{"type":"View","width":300}"""), 0f, 1f)
        assertEquals(0f to 1f, com.kotlinjsonui.dynamic.helpers.ModifierBuilder.overflowBias(sized))
        val wrapped = json("""{"type":"View","width":"wrapContent","height":"matchParent"}""")
        assertTrue(DynamicContainerComponent.withOverflowBias(wrapped, 0f, 1f) === wrapped)
        val bound = json("""{"type":"View","width":"@{w}"}""")
        assertTrue(DynamicContainerComponent.withOverflowBias(bound, 0f, 1f) === bound)
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
