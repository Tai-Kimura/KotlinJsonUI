package com.kotlinjsonui.dynamic

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.components.SegmentTab
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/**
 * A Segment's own width, as iOS's UISegmentedControl and the web draw it (jsonui-cli conformance
 * Segment/items__wrap_uneven and __control/Segment__width-wrapContent, 2.5.47).
 *
 * wrapContent: as wide as its labels need, not the width it is offered. Every segment as wide as
 * the longest label needs, n of them plus the 2 dp inset each side, inside the parent, no label
 * cut; a maxWidth bounds it. A fixed width (a number, matchParent) is taken whole, as before.
 * The parent here is 360 dp wide; the Segment used to take all 360 at wrapContent, its first
 * label far right of where iOS draws it and the others past the screen's edge.
 */
@RunWith(AndroidJUnit4::class)
class SegmentWidthTest {
    @get:Rule val rule = createComposeRule()
    private var content by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var composed = false
    private val labels = listOf("あ", "あいうえお", "かき")

    private fun show(draw: @Composable () -> Unit) {
        if (!composed) {
            rule.setContent {
                MaterialTheme {
                    content?.let { c -> key(c) { Box(Modifier.width(360.dp).background(Color.White).testTag("parent")) { c() } } }
                }
            }
            composed = true
        }
        rule.runOnIdle { content = null }
        rule.waitForIdle()
        rule.runOnIdle { content = draw }
        rule.waitForIdle()
    }

    private fun static(modifier: Modifier): @Composable () -> Unit = {
        Segment(selectedTabIndex = 0, modifier = modifier.testTag("seg")) {
            labels.forEachIndexed { i, label ->
                SegmentTab(selected = i == 0, onClick = {}, modifier = Modifier.testTag("seg_tab_$i"), text = {
                    Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                })
            }
        }
    }

    private fun dynamic(width: String): @Composable () -> Unit = {
        DynamicView(
            json = JsonParser.parseString(
                """{"type": "Segment", "id": "seg", $width, "items": ["あ", "あいうえお", "かき"], "selectedIndex": 0}"""
            ).asJsonObject,
            data = emptyMap(),
        )
    }

    private fun cut(text: String): Boolean {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
        return results.single().isLineEllipsized(0)
    }

    /** The Segment's width, and whether its segments are equal, inside it and nothing cut. */
    private fun row(name: String): String {
        val seg = rule.onNodeWithTag("seg", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val parent = rule.onNodeWithTag("parent").fetchSemanticsNode().boundsInRoot
        val tabs = labels.indices.map { rule.onNodeWithTag("seg_tab_$it", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot }
        val d = rule.density.density
        val w = seg.width / d
        val equal = tabs.all { abs(it.width - tabs[0].width) <= 1f }
        val inside = tabs.all { it.left >= seg.left - 0.5f && it.right <= seg.right + 0.5f } && seg.right <= parent.right + 0.5f
        // As wide as the labels need: n segments of the longest label's width plus the inset, to
        // a dp (the longest label's segment is its text and the 6 dp padding each side).
        val needs = labels.indices.maxOf { rule.onNodeWithText(labels[it], useUnmergedTree = true).fetchSemanticsNode().size.width } / d + 12f
        val natural = abs(w - (needs * labels.size + 4f)) <= 1f
        val cut = labels.any { cut(it) }
        return "$name :: width %s, equal $equal, inside $inside, cut $cut".format(
            // The declared numbers first: a cut label's text is narrower, so at 100 the labels'
            // "need" is read from the cut text and would match too.
            when {
                listOf(360f, 240f, 100f).any { abs(w - it) <= 0.5f } -> "%.0f".format(w)
                natural -> "natural"
                else -> "%.1f".format(w)
            }
        )
    }

    @Test
    fun wrapContentIsAsWideAsTheLabelsNeed_aFixedWidthIsTakenWhole() {
        val rows = mutableListOf<String>()
        for ((name, draw) in listOf<Pair<String, @Composable () -> Unit>>(
            "static wrapContent" to static(Modifier.wrapContentWidth()),
            "dynamic wrapContent" to dynamic(""""width": "wrapContent""""),
            "static matchParent" to static(Modifier.fillMaxWidth()),
            "dynamic matchParent" to dynamic(""""width": "matchParent""""),
            "static 240" to static(Modifier.width(240.dp)),
            "dynamic 240" to dynamic(""""width": 240"""),
            "static wrapContent, maxWidth 100" to static(Modifier.wrapContentWidth().widthIn(max = 100.dp)),
            "dynamic wrapContent, maxWidth 100" to dynamic(""""width": "wrapContent", "maxWidth": 100"""),
        )) {
            show(draw)
            rows += row(name)
        }
        val table = rows.joinToString("\n")
        Log.i("KjuiSegmentWidth", "\n$table")
        val expected = listOf(
            "static wrapContent :: width natural, equal true, inside true, cut false",
            "dynamic wrapContent :: width natural, equal true, inside true, cut false",
            "static matchParent :: width 360, equal true, inside true, cut false",
            "dynamic matchParent :: width 360, equal true, inside true, cut false",
            "static 240 :: width 240, equal true, inside true, cut false",
            "dynamic 240 :: width 240, equal true, inside true, cut false",
            // Bounded at 100: the segments share it, and the longest label no longer fits.
            "static wrapContent, maxWidth 100 :: width 100, equal true, inside true, cut true",
            "dynamic wrapContent, maxWidth 100 :: width 100, equal true, inside true, cut true",
        ).joinToString("\n")
        assertEquals(expected, table)
    }
}
