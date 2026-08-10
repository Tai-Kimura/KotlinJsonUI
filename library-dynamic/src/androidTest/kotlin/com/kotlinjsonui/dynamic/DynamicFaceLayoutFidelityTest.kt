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
                // The codegen emit shape, verbatim (BusinessHourRowGeneratedView).
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
              "cell": {
                "type": "Label",
                "width": "wrapContent",
                "height": "wrapContent",
                "minHeight": 36,
                "paddings": [5, 16],
                "text": "lazyCell",
                "fontSize": 13
              }
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
                DynamicView(json = json, data = emptyMap())
            }
        }
        rule.waitForIdle()

        // Self-calibrating: the cell inside the lazy lane must measure like
        // the identical label rendered standalone — a lane-stretch (the old
        // LazyHorizontalGrid semantics) breaks the equality.
        val ref = rule.onNodeWithTag("ref").fetchSemanticsNode()
        val refTextH = rule.onAllNodesWithText("refCell").fetchSemanticsNodes().first().size.height
        val nodes = rule.onAllNodesWithText("lazyCell")
            .fetchSemanticsNodes()
            .sortedBy { it.positionInRoot.x }
        assertTrue("expected several cells, got ${nodes.size}", nodes.size >= 2)
        assertEquals(
            "in-lane cell text height must equal the standalone reference",
            refTextH, nodes[0].size.height
        )
        val refHDp = ref.size.height / density
        assertTrue("reference chip must be its declared 36dp, was ${refHDp}dp", abs(refHDp - 36f) <= 3f)
        // Text nodes sit inside the cells' 16dp horizontal padding, so the
        // text-to-text gap is padding + lineSpacing + padding = 40dp.
        val gapPx = nodes[1].positionInRoot.x - (nodes[0].positionInRoot.x + nodes[0].size.width)
        val gapDp = (gapPx / density).roundToInt()
        assertEquals(
            "text-to-text gap must be 16+8+16dp (cell padding + lineSpacing), was ${gapDp}dp",
            40, gapDp
        )
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
              "cell": {
                "type": "Label",
                "width": "wrapContent",
                "height": "wrapContent",
                "text": "cellX",
                "fontSize": 13
              }
            }
            """.trimIndent()
        ).asJsonObject

        var density = 0f
        rule.setContent {
            density = androidx.compose.ui.platform.LocalDensity.current.density
            DynamicView(json = json, data = emptyMap())
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
