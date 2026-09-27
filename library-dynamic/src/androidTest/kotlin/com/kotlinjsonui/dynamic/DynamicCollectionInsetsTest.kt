package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.toSize
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A Collection's insetHorizontal / insetVertical and the safe area its
 * contentInsetAdjustmentBehavior asks for are ADDED, as iOS adds them (4f
 * ruling 2026-09-27, round 16) — measured on sjui codegen and SwiftJsonUI
 * Dynamic: a Collection at the top of a 62pt safe area with insetVertical 8
 * put its first cell at 70, `always` alike, and at 8 with `never`. Until
 * jsonui-cli 1.9.0 the safe area replaced them here. A declared insets still
 * wins over both (plan 49 lane C, #4).
 *
 * The activity draws edge to edge, so the Collection at the top of the
 * content is under the status bar and the safe area is not 0 there (the arm
 * says so when it is).
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionInsetsTest {

    @get:Rule
    val rule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    private val cell = "collection_probe_title_cell"
    private val twoCells = CollectionDataSource(
        sections = listOf(CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf("s0", "s1").map { mapOf<String, Any>("title" to it) })))
    )

    private var shown by mutableStateOf<JsonObject?>(null)
    private var safeTopPx = -1

    private fun show(extra: String) {
        if (safeTopPx < 0) {
            rule.runOnUiThread { androidx.core.view.WindowCompat.setDecorFitsSystemWindows(rule.activity.window, false) }
            rule.setContent {
                safeTopPx = WindowInsets.safeDrawing.getTop(LocalDensity.current)
                shown?.let { j -> key(j) { DynamicView(json = j, data = mapOf("items" to twoCells, "t" to 20)) } }
            }
        }
        rule.runOnIdle {
            shown = JsonParser.parseString(
                """{"type": "Collection", "id": "list", "items": "@{items}", "width": 200, "height": 300,
                    "sections": [{"cell": "$cell"}] $extra}"""
            ).asJsonObject
        }
        rule.waitForIdle()
    }

    private fun cellTop(): Float {
        val list = rule.onNodeWithTag("list", useUnmergedTree = true).fetchSemanticsNode().positionInRoot.y
        val s0 = rule.onAllNodesWithText("s0").fetchSemanticsNodes().single()
        return Rect(s0.positionInRoot, s0.size.toSize()).top - list
    }

    private fun cellLeft(): Float {
        val list = rule.onNodeWithTag("list", useUnmergedTree = true).fetchSemanticsNode().positionInRoot.x
        return rule.onAllNodesWithText("s0").fetchSemanticsNodes().single().positionInRoot.x - list
    }

    /**
     * A declared insets, insetHorizontal / insetVertical and the safe area
     * are all added, side by side, as iOS adds them (measured: insets
     * [8,0,0,0] with insetVertical 8 at the top of a 62pt safe area, the
     * first cell at 78). Until jsonui-cli 1.9.0 the declared insets replaced
     * the other two (4f ruling 2026-09-27, round 17). A binding in the insets
     * array is resolved from the data (t = 20); the array declared nothing.
     */
    @Test
    fun aDeclaredInsetsIsAddedToTheOthers() {
        show("")
        val px = { dp: Int -> with(rule.density) { dp.toFloat() * density } }
        assertTrue("the safe area is 0 here: the arm cannot tell adding from replacing", safeTopPx > 0)
        val safe = safeTopPx.toFloat()
        val cases = listOf(
            """, "insets": [8, 0, 0, 0], "insetVertical": 8, "insetHorizontal": 16""" to (px(16) to px(16)),
            """, "insets": [8, 0, 0, 0], "insetVertical": 8, "contentInsetAdjustmentBehavior": "always"""" to (safe + px(16) to 0f),
            """, "insets": [8, 0, 0, 0], "contentInsetAdjustmentBehavior": "always"""" to (safe + px(8) to 0f),
            """, "insets": [8, 4, 8, 6], "insetHorizontal": 10""" to (px(8) to px(16)),
            """, "insets": ["@{t}", 0, 0, 0]""" to (px(20) to 0f),
            """, "insets": ["@{t}", 0, 0, 0], "insetVertical": 4""" to (px(24) to 0f),
            """, "insets": [8, 0, 0, 0]""" to (px(8) to 0f),
            // Start and end in the layout's direction, beside the safe area: left (the start) 6.
            """, "insets": [0, 4, 0, 6], "contentInsetAdjustmentBehavior": "always"""" to (safe to px(6)),
        )
        val drawn = cases.map { (extra, _) -> show(extra); "%.0f,%.0f".format(cellTop(), cellLeft()) }
        assertEquals(cases.map { "%.0f,%.0f".format(it.second.first, it.second.second) }, drawn)
    }

    @Test
    fun insetsAreAddedToTheSafeArea() {
        show("")
        val px = { dp: Int -> with(rule.density) { dp.toFloat() * density } }
        assertTrue("the safe area is 0 here: the arm cannot tell adding from replacing", safeTopPx > 0)
        val safe = safeTopPx.toFloat()
        val cases = listOf(
            """, "insetVertical": 8, "insetHorizontal": 16, "contentInsetAdjustmentBehavior": "always"""" to safe + px(8),
            """, "insetVertical": 8, "contentInsetAdjustmentBehavior": "scrollableAxes"""" to safe + px(8),
            """, "insetVertical": 8, "contentInsetAdjustmentBehavior": "never"""" to px(8),
            """, "contentInsetAdjustmentBehavior": "always"""" to safe,
            """, "insets": "1|2|3", "contentInsetAdjustmentBehavior": "always"""" to safe,
            "" to 0f,
        )
        val drawn = cases.map { (extra, _) -> show(extra); "%.0f".format(cellTop()) }
        assertEquals(cases.map { "%.0f".format(it.second) }, drawn)
    }
}
