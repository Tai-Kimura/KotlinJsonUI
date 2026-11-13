package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A SelectBox / DateSelectBox is as tall as its layout says (ticket kjui-selectbox-fixed-56dp-ignores-wrap-content-
 * height): with wrapContent (and a minHeight) its text and its paddings — a value folded to 2 lines grows it, where a
 * `.height(56.dp)` after the caller's modifier kept it at 56 and cut the second line; with a fixed height, that height,
 * the text centred as before; with none, its content (no height of its own: the 3 faces' rule, 2026-11-13).
 * Drawn at a font scale of 2.0, the size at which client's long values fold.
 */
@RunWith(AndroidJUnit4::class)
class SelectBoxHeightTest {
    @get:Rule val rule = createComposeRule()

    private val long = "Northwind Trading Company Seasonal Product Collection"

    private fun content(select: @androidx.compose.runtime.Composable (Modifier) -> Unit) = rule.setContent {
        val d = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = 2f)) {
            Column(Modifier.width(320.dp)) {
                select(Modifier.fillMaxWidth().wrapContentHeight().heightIn(min = 48.dp).testTag("wrap"))
                select(Modifier.fillMaxWidth().requiredHeight(48.dp).testTag("fixed"))
                select(Modifier.fillMaxWidth().testTag("none"))
            }
        }
    }

    private fun height(tag: String) = rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.height

    private fun checks(density: Float) {
        val px = { dp: Float -> dp * density }
        // wrapContent: the folded text and the 12 + 12 dp paddings, well past 56 dp.
        assertTrue("wrapContent grows with its 2 lines: ${height("wrap") / density} dp", height("wrap") > px(56f) + 1f)
        // A fixed height is that height.
        assertEquals(px(48f), height("fixed"), 1f)
        // No height: its content, not a 56 dp of its own.
        assertTrue("no height is its content: ${height("none") / density} dp", height("none") > px(56f) + 1f)
    }

    @Test fun a_select_box_is_as_tall_as_its_layout_says() {
        content { m ->
            SelectBox(value = long, onValueChange = {}, options = listOf(long), modifier = m,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp))
        }
        rule.waitForIdle()
        checks(rule.density.density)
        centred()
    }

    /** The fixed one keeps its text centred, as before (the text taller than 48 dp overflows both ways alike). */
    private fun centred() {
        val box = rule.onNodeWithTag("fixed").fetchSemanticsNode().boundsInRoot
        val text = rule.onAllNodesWithText(long, useUnmergedTree = true).fetchSemanticsNodes()
            .map { it.boundsInRoot }.minBy { kotlin.math.abs(it.center.y - box.center.y) }
        assertEquals("the text centred in the fixed height", box.center.y, text.center.y, 2f * rule.density.density)
    }

    /**
     * With no height and no paddings, a select is one line of its text: no padding above or below (SelectBoxDefaults,
     * the three faces' default of 0) and no height of its own — measured against a Text of the same size alone.
     */
    @Test fun with_no_height_and_no_paddings_a_select_is_one_line_of_its_text() {
        rule.setContent {
            Column(Modifier.width(320.dp)) {
                androidx.compose.material3.Text("A", fontSize = 16.sp, modifier = Modifier.testTag("line"))
                SelectBox(value = "A", onValueChange = {}, options = listOf("A"), modifier = Modifier.fillMaxWidth().testTag("select"))
                DateSelectBox(value = "2026-11-13", onValueChange = {}, modifier = Modifier.fillMaxWidth().testTag("date"))
            }
        }
        rule.waitForIdle()
        val d = rule.density.density
        val line = height("line")
        assertEquals("SelectBox: one line", line, height("select"), 2f * d)
        assertEquals("DateSelectBox: one line", line, height("date"), 2f * d)
        // The text from 12 dp after the leading edge to 36 dp before the trailing one: 12 dp to the arrow's box, its
        // 16 dp, 8 dp between (SelectBoxDefaults, the three faces' layout).
        for ((tag, text) in listOf("select" to "A", "date" to "2026-11-13")) {
            val box = rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
            val t = rule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().map { it.boundsInRoot }
                .first { it.center.y > box.top && it.center.y < box.bottom && it.left >= box.left }
            assertEquals("$tag: text starts 12 dp in", box.left + 12f * d, t.left, 1f)
            assertEquals("$tag: text stops 36 dp before the edge", box.right - 36f * d, t.right, 1f)
        }
    }

    @Test fun a_date_select_box_is_as_tall_as_its_layout_says() {
        content { m -> DateSelectBox(value = long, onValueChange = {}, modifier = m) }
        rule.waitForIdle()
        val density = rule.density.density
        // DateSelectBox pads 16 dp on the sides only: wrapped, its 2 lines (over the 48 dp minimum).
        assertTrue("wrapContent grows with its 2 lines: ${height("wrap") / density} dp", height("wrap") > 56f * density + 1f)
        assertEquals(48f * density, height("fixed"), 1f)
        assertTrue("no height is its content: ${height("none") / density} dp", height("none") > 56f * density + 1f)
        centred()
    }
}
