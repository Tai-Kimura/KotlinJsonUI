package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Instrumented pins for dynamic-face layout fidelity, measured in real
 * composition (downstream reports, 2026-08-10):
 *
 * 1. A chip-shaped Label (wrapContent + minHeight + paddings) measures the
 *    declared envelope — minHeight is the OUTER minimum including padding,
 *    exactly what the codegen emits (defaultMinSize before padding).
 * 2. A horizontal collection separates its cells along the scroll axis by
 *    lineSpacing (the codegen face's `line_spacing || column_spacing` fold).
 */
@RunWith(AndroidJUnit4::class)
class DynamicFaceLayoutFidelityTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun hourRowShapedCellMatchesTheCodegenEmitHeight() {
        // A downstream hour-row cell: horizontal row, two fontSize-14
        // labels (one plain, one partialAttributes), paddingVertical 6.
        // The codegen face emits lineHeight = 14*1.3 = 18.2sp on the plain
        // label; the row measures padding + that line. The dynamic render
        // must measure the same (user report: dynamic rows ~3dp shorter,
        // 2026-08-10).
        val row = JsonParser.parseString(
            """
            {
              "type": "View", "width": "matchParent", "height": "wrapContent",
              "orientation": "horizontal", "gravity": "centerVertical",
              "leftPadding": 16, "rightPadding": 16, "paddingTop": 6, "paddingBottom": 6,
              "child": [
                { "type": "Label", "id": "day", "width": 40, "height": "wrapContent",
                  "text": "月", "fontSize": 14, "rightMargin": 16 },
                { "type": "Label", "id": "hours", "height": "wrapContent", "weight": 1,
                  "text": "18:00 - 2:00", "fontSize": 14,
                  "partialAttributes": [{"range": "@{overrideBoldRange}", "font": "bold"}] }
              ]
            }
            """.trimIndent()
        ).asJsonObject

        var density = 0f
        rule.setContent {
            density = androidx.compose.ui.platform.LocalDensity.current.density
            // A REAL ambient line height, like an app theme's typography —
            // without it both faces coincide trivially and the partial-path
            // lineHeight divergence hides (the false pass this test first
            // produced).
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalTextStyle provides
                    androidx.compose.material3.LocalTextStyle.current.copy(
                        lineHeight = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp)
                    )
            ) {
            androidx.compose.foundation.layout.Column {
                Box(Modifier.testTag("dyn")) { DynamicView(json = row, data = emptyMap()) }
                // The codegen emit shape, verbatim (the downstream row's GeneratedView).
                Box(Modifier.testTag("gen")) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.then(
                            androidx.compose.foundation.layout.PaddingValues(
                                start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp
                            ).let { pv -> Modifier.padding(pv) }
                        ),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(
                            text = "月",
                            fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp),
                            style = androidx.compose.material3.LocalTextStyle.current.copy(
                                lineHeight = androidx.compose.ui.unit.TextUnit(18.2f, androidx.compose.ui.unit.TextUnitType.Sp)
                            )
                        )
                        // The partial half, exactly as the codegen emits it:
                        // style WITHOUT a lineHeight (the partial style_parts
                        // never carry one).
                        com.kotlinjsonui.components.PartialAttributesText(
                            text = "18:00 - 2:00",
                            partialAttributes = emptyList(),
                            style = androidx.compose.material3.LocalTextStyle.current.copy(
                                fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)
                            )
                        )
                    }
                }
            }
            }
        }
        rule.waitForIdle()
        val dynH = rule.onNodeWithTag("dyn").fetchSemanticsNode().size.height / density
        val genH = rule.onNodeWithTag("gen").fetchSemanticsNode().size.height / density
        // Face parity, measured against the verbatim codegen emit composed
        // in the same ambient (both 32.0dp on phone_ci at density 2.625).
        assertTrue(
            "dynamic hour-row ${dynH}dp must equal the codegen emit ${genH}dp",
            abs(dynH - genH) <= 0.5f
        )
    }

    @Test
    fun chipShapedLabelMeasuresItsDeclaredEnvelope() {
        val json = JsonParser.parseString(
            """
            {
              "type": "Label",
              "id": "chip",
              "width": "wrapContent",
              "height": "wrapContent",
              "minHeight": 36,
              "text": "@{chipText}",
              "fontSize": 13,
              "textAlign": "center",
              "gravity": "center",
              "borderWidth": 1,
              "borderColor": "#FFD700",
              "cornerRadius": 18,
              "paddings": [5, 16]
            }
            """.trimIndent()
        ).asJsonObject

        var density = 0f
        rule.setContent {
            density = androidx.compose.ui.platform.LocalDensity.current.density
            Box(Modifier.testTag("wrap")) {
                DynamicView(json = json, data = mapOf("chipText" to "飲みました!"))
            }
        }
        rule.waitForIdle()

        val h = rule.onNodeWithTag("wrap").fetchSemanticsNode().size.height
        val hDp = h / density
        // 13sp text + 5dp vertical padding ×2 stays under the 36dp minimum,
        // so the envelope IS the minimum. A tolerance of 3dp absorbs font
        // metric variance; the 2.21.x report measured ~+10dp when broken.
        assertTrue(
            "chip-shaped label measures ${hDp}dp; declared envelope is 36dp",
            abs(hDp - 36f) <= 3f
        )
    }

    @Test
    fun lazyHorizontalSingleLaneKeepsCellCrossSizeAndSpacing() {
        // The downstream chip-carousel shape: an 80dp-tall LAZY horizontal collection of
        // chip-shaped cells. The cells must keep their OWN height (36dp), not
        // stretch to the lane, and must sit lineSpacing (8dp) apart.
        val json = JsonParser.parseString(
            """
            {
              "type": "Collection",
              "id": "chips",
              "width": "matchParent",
              "height": 80,
              "orientation": "horizontal",
              "lineSpacing": 8,
              "items": "@{items}",
              "sections": [{ "cell": "collection_probe_lazy_chip_cell" }]
            }
            """.trimIndent()
        ).asJsonObject

        val refJson = JsonParser.parseString(
            """
            {
              "type": "Label", "width": "wrapContent", "height": "wrapContent",
              "minHeight": 36, "paddings": [5, 16], "text": "refCell", "fontSize": 13
            }
            """.trimIndent()
        ).asJsonObject

        var density = 0f
        rule.setContent {
            density = androidx.compose.ui.platform.LocalDensity.current.density
            androidx.compose.foundation.layout.Column {
                Box(Modifier.testTag("ref")) { DynamicView(json = refJson, data = emptyMap()) }
                DynamicView(json = json, data = mapOf("items" to probeItems(10, "collection_probe_lazy_chip_cell")))
            }
        }
        rule.waitForIdle()

        // Self-calibrating: the cell inside the lazy lane must measure like
        // the identical label rendered standalone — a lane-stretch (the old
        // LazyHorizontalGrid semantics) breaks the equality.
        //
        // Measured on the CELLS (their `chips_item_<n>` nodes), not on their
        // text. A drawn cell carries the item test tag on its root, outside
        // its padding, and the label's text semantics merge into that tagged
        // node — so the text node of a cell spans the padding and the 36dp
        // minimum (72 px here) while the standalone label's text node sits
        // inside its padding (52 px). Comparing the two text nodes compared a
        // semantics boundary, not a size: red since 958584b moved this test
        // from the node-level template to a sections cell (bisected
        // 2026-09-26: green on 462d571; red on 8a1f51c, and red with
        // 958584b's test on 958584b's parent's code — the drawn cells were
        // 36dp tall and 8dp apart throughout).
        val ref = rule.onNodeWithTag("ref").fetchSemanticsNode()
        val cells = (0 until 3).map { rule.onNodeWithTag("chips_item_$it").fetchSemanticsNode() }
        val refHDp = ref.size.height / density
        assertTrue("reference chip must be its declared 36dp, was ${refHDp}dp", abs(refHDp - 36f) <= 3f)
        for ((i, cell) in cells.withIndex()) {
            assertEquals("cell $i height must equal the standalone reference (not the 80dp lane)", ref.size.height, cell.size.height)
        }
        // Cells sit lineSpacing (8dp) apart along the scroll axis.
        for (i in 0 until cells.size - 1) {
            val gapPx = cells[i + 1].positionInRoot.x - (cells[i].positionInRoot.x + cells[i].size.width)
            assertEquals("cell $i to ${i + 1}: lineSpacing 8dp, was ${gapPx / density}dp", 8, (gapPx / density).roundToInt())
        }
    }

    @Test
    fun horizontalCollectionSpacesCellsAlongTheScrollAxis() {
        val json = JsonParser.parseString(
            """
            {
              "type": "Collection",
              "id": "chips",
              "width": "matchParent",
              "height": 40,
              "orientation": "horizontal",
              "lazy": "none",
              "lineSpacing": 8,
              "items": "@{items}",
              "sections": [{ "cell": "collection_probe_chip_cell" }]
            }
            """.trimIndent()
        ).asJsonObject

        var density = 0f
        rule.setContent {
            density = androidx.compose.ui.platform.LocalDensity.current.density
            DynamicView(json = json, data = mapOf("items" to probeItems(10, "collection_probe_chip_cell")))
        }
        rule.waitForIdle()

        val nodes = rule.onAllNodesWithText("cellX")
            .fetchSemanticsNodes()
            .sortedBy { it.positionInRoot.x }
        assertTrue("expected several cells, got ${nodes.size}", nodes.size >= 2)
        val gapPx = nodes[1].positionInRoot.x - (nodes[0].positionInRoot.x + nodes[0].size.width)
        val gapDp = (gapPx / density).roundToInt()
        assertEquals(
            "horizontal cell gap must be lineSpacing (8dp), was ${gapDp}dp",
            8, gapDp
        )
    }
}
