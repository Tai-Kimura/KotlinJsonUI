package com.kotlinjsonui.dynamic

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
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

/**
 * A Segment drawn as iOS draws it (the requester's ruling 2026-11-15, jsonui-cli ticket
 * jui-segment-android-draws-no-track, option (a)), static (the calls kjui generates) and Dynamic:
 * a capsule track (its corners clear, its colour iOS's translucent grey on white: 238,238,239),
 * the selected segment filled with tintColor, each label on one line. It was Material's TabRow:
 * no track, an underline, "Gamma" wrapped in a 200 wide Segment.
 */
@RunWith(AndroidJUnit4::class)
class SegmentFormTest {
    @get:Rule val rule = createComposeRule()
    private var content by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var composed = false
    private val items = listOf("Alpha", "Beta", "Gamma")

    private fun read(name: String, draw: @Composable () -> Unit): String {
        if (!composed) {
            rule.setContent { content?.let { c -> key(c) { Box(Modifier.background(Color.White).padding(8.dp)) { c() } } } }
            composed = true
        }
        rule.runOnIdle { content = null }
        rule.waitForIdle()
        rule.runOnIdle { content = draw }
        rule.waitForIdle()
        Thread.sleep(400)
        rule.waitForIdle()
        val map = rule.onNodeWithTag("seg", useUnmergedTree = true).captureToImage().toPixelMap()
        fun at(fx: Float, fy: Float) = map[(map.width * fx).toInt().coerceIn(0, map.width - 1), (map.height * fy).toInt().coerceIn(0, map.height - 1)]
        fun name(c: Color) = when {
            c.red > 0.95f && c.green > 0.95f && c.blue > 0.95f -> "white"
            c.red > 0.85f && c.green < 0.15f && c.blue < 0.15f -> "tint"
            kotlin.math.abs(c.red * 255 - 238) < 4 && kotlin.math.abs(c.green * 255 - 238) < 4 && kotlin.math.abs(c.blue * 255 - 239) < 4 -> "track"
            else -> "(%d,%d,%d)".format((c.red * 255).toInt(), (c.green * 255).toInt(), (c.blue * 255).toInt())
        }
        // The corner, a strip of the selected segment above its label, and one of the unselected's.
        val corner = name(at(0f, 0f))
        val selected = name(at(1f / 6, 0.15f))
        val unselected = name(at(5f / 6, 0.15f))
        val lines = rule.onAllNodesWithText("Gamma", useUnmergedTree = true).fetchSemanticsNodes().single().size.height
        val oneLine = lines < with(rule.density) { 26.dp.toPx() }
        return "$name :: corner $corner, selected $selected, unselected $unselected, one line $oneLine"
    }

    @Test
    fun aTrackAndATintedCapsuleOnTheSelectedSegment_labelsOnOneLine() {
        val rows = mutableListOf<String>()
        rows += read("static") {
            // kjui's call for {"items": ["Alpha","Beta","Gamma"], "width": 200, "tintColor": "#FF0000"}.
            Segment(selectedTabIndex = 0, modifier = Modifier.width(200.dp).testTag("seg"), indicatorColor = Color.Red) {
                items.forEachIndexed { i, label ->
                    SegmentTab(selected = i == 0, onClick = {}, text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) })
                }
            }
        }
        rows += read("dynamic") {
            DynamicView(
                json = JsonParser.parseString(
                    """{"type": "Segment", "id": "seg", "width": 200, "items": ["Alpha", "Beta", "Gamma"], "tintColor": "#FF0000"}"""
                ).asJsonObject,
                data = emptyMap(),
            )
        }
        val table = rows.joinToString("\n")
        Log.i("KjuiSegmentForm", "\n$table")
        val expected = listOf("static", "dynamic").joinToString("\n") {
            "$it :: corner white, selected tint, unselected track, one line true"
        }
        assertEquals(expected, table)
    }
}
