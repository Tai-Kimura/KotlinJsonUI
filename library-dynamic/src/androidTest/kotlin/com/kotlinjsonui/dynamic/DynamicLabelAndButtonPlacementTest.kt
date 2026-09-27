package com.kotlinjsonui.dynamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Where a Label's text and a Button's text sit, read from the PIXELS of a box
 * this test owns (not an accessibility frame): white is the Label's or
 * Button's background, black its text, the box red.
 *
 * - A Label's text in a frame taller than it sits by its gravity's vertical
 *   part — top, bottom, the middle for center / centerVertical — and in the
 *   middle when the gravity names none (4f ruling 2026-09-27, round 17;
 *   gravityDefaults -> leafOwnFrameChannel), as iOS places it. It sat at the
 *   top whatever the gravity. The background fills the Label's frame.
 * - A Button's text is placed across it by textAlign, centre by default: it
 *   sat in the middle whatever textAlign said.
 */
@RunWith(AndroidJUnit4::class)
class DynamicLabelAndButtonPlacementTest {

    @get:Rule
    val rule = createComposeRule()

    private var shown by mutableStateOf<JsonObject?>(null)
    private var boxColor by mutableStateOf(Color.Red)
    private var composed = false

    private fun show(json: String) {
        if (!composed) {
            rule.setContent {
                Box(Modifier.testTag("box").background(boxColor)) {
                    shown?.let { j -> key(j) { DynamicView(json = j, data = emptyMap()) } }
                }
            }
            composed = true
        }
        rule.runOnIdle { shown = JsonParser.parseString(json).asJsonObject }
        rule.waitForIdle()
    }

    /** The rows (or columns) the box's white and black pixels span, in dp: "white a..b, black c..d". */
    private fun extent(columns: Boolean = false): Pair<IntRange?, IntRange?> {
        val map = rule.onNodeWithTag("box").captureToImage().toPixelMap()
        val d = rule.density.density
        val outer = if (columns) map.width else map.height
        val inner = if (columns) map.height else map.width
        var white: IntRange? = null
        var black: IntRange? = null
        for (i in 0 until outer) {
            var w = false
            var b = false
            for (j in 0 until inner) {
                val c = if (columns) map[i, j] else map[j, i]
                if (c.red > 0.94f && c.green > 0.94f && c.blue > 0.94f) w = true
                if (c.red < 0.25f && c.green < 0.25f && c.blue < 0.25f) b = true
            }
            val dp = (i / d).toInt()
            if (w) white = white?.let { it.first..dp } ?: dp..dp
            if (b) black = black?.let { it.first..dp } ?: dp..dp
        }
        return white to black
    }

    private fun label(extra: String) =
        """{"type": "Label", "id": "l", "text": "8", "fontColor": "#000000", "background": "#FFFFFF" $extra}"""

    /**
     * Where the text's ink sits in the frame: the middle (as much above as
     * below, within 2dp), top or bottom (the ink sits a font's leading off the
     * edge — 7dp for a 24sp digit — so these are "nearer the top / bottom by
     * more than 4dp"), or the numbers.
     */
    private fun place(frame: IntRange, text: IntRange): String {
        val above = text.first - frame.first
        val below = frame.last - text.last
        return when {
            kotlin.math.abs(above - below) <= 2 -> "middle"
            above + 4 < below -> "top"
            below + 4 < above -> "bottom"
            else -> "at ${above}..${below}"
        }
    }

    @Test
    fun aLabelsTextSitsInItsFrameByItsGravity() {
        val cases = listOf(
            // The shapes the faces declare: a 2FA digit box, a chip with a minHeight, and — below — a matchParent guide.
            """, "width": 44, "height": 56, "fontSize": 24, "font": "bold", "textAlign": "center", "gravity": "center"""" to "middle",
            """, "width": "wrapContent", "height": "wrapContent", "minHeight": 36, "paddings": [5, 16], "fontSize": 13, "gravity": "center"""" to "middle",
            """, "width": 44, "height": 56, "fontSize": 24, "gravity": "top"""" to "top",
            """, "width": 44, "height": 56, "fontSize": 24, "gravity": "bottom"""" to "bottom",
            """, "width": 44, "height": 56, "fontSize": 24, "gravity": "centerVertical"""" to "middle",
            """, "width": 44, "height": 56, "fontSize": 24""" to "middle",
            """, "width": 44, "height": 56, "fontSize": 24, "gravity": "left"""" to "middle",
        )
        val drawn = cases.map { (extra, _) ->
            show(label(extra))
            val (white, black) = extent()
            if (white == null || black == null) "not drawn" else "${place(white, black)} (frame ${white.last - white.first + 1}dp, ink ${black.first - white.first}..${black.last - white.first})"
        }
        assertEquals(cases.map { it.second }, drawn.map { it.substringBefore(" (") })
        // The 2FA digit is wholly inside its 56dp box; the chip's background is its 36dp.
        println("LABEL_PLACEMENT " + drawn.joinToString(" | "))
        val digit = drawn[0]
        assertTrue(digit, digit.contains("(frame 56dp") || digit.contains("(frame 57dp"))
        val ink = Regex("ink (\\d+)\\.\\.(\\d+)").find(digit)!!.groupValues
        assertTrue("the digit's ink inside the 56dp box: $digit", ink[1].toInt() > 0 && ink[2].toInt() < 55)
        assertTrue(drawn[1], drawn[1].contains("(frame 36dp") || drawn[1].contains("(frame 37dp"))
    }

    @Test
    fun aLabelFillingItsParentCentresItsText() {
        show(
            """{"type": "View", "id": "strip", "width": 200, "height": 122, "background": "#FFFFFF", "child": [
                ${label(""", "width": "matchParent", "height": "matchParent", "lines": 0, "fontSize": 16, "lineSpacing": 6,
                    "paddingLeft": 24, "paddingRight": 24, "textAlign": "center", "gravity": "center", "text": "guide text of two lines here"""")}]}"""
        )
        val (white, black) = extent()
        assertTrue("not drawn", white != null && black != null)
        println("LABEL_PLACEMENT guide frame ${white!!.last - white.first + 1}dp ink ${black!!.first - white.first}..${black.last - white.first}")
        assertEquals("the guide in its 122dp strip", "middle", place(white, black))
    }

    /**
     * Without textAlign, a Label's text is placed across a frame wider than it
     * by its gravity's horizontal part (4f ruling 2026-09-27, round 17);
     * textAlign still wins. It sat at the start whatever the gravity.
     */
    @Test
    fun aLabelsTextSitsAcrossItsFrameByItsGravity() {
        val cases = listOf(
            // The face's intensity value: 32 wide, gravity right.
            """, "width": 32, "fontSize": 14, "text": "4", "gravity": "right"""" to "right",
            """, "width": 200, "fontSize": 14, "gravity": "center"""" to "middle",
            """, "width": 200, "fontSize": 14, "gravity": "centerHorizontal"""" to "middle",
            """, "width": 200, "fontSize": 14, "gravity": "right", "textAlign": "left"""" to "left",
            """, "width": 200, "fontSize": 14""" to "left",
        )
        val drawn = cases.map { (extra, _) ->
            show(label(extra))
            val (white, black) = extent(columns = true)
            if (white == null || black == null) "not drawn" else {
                val left = black.first - white.first
                val right = white.last - black.last
                println("LABEL_ACROSS $extra: ink ${left}..${right} of ${white.last - white.first + 1}")
                when {
                    kotlin.math.abs(left - right) <= 2 -> "middle"
                    left < right -> "left"
                    else -> "right"
                }
            }
        }
        assertEquals(cases.map { it.second }, drawn)
    }

    /**
     * A Button with an icon, and a loading one: the icon (the spinner) and
     * the text move together, placed across the button by textAlign — the
     * group at the start / end / middle, as iOS places it (measured round 18:
     * sjui codegen and SwiftJsonUI Dynamic, a 200pt button, the icon + text
     * group at 0..43, 156..198 and 78..120). They sat in the middle whatever
     * textAlign said. The group's width is the same in every place.
     */
    @Test
    fun aButtonsIconAndTextMoveTogetherByTextAlign() {
        fun button(extra: String) =
            """{"type": "Button", "id": "b", "text": "Go", "width": 200, "height": 44, "fontSize": 14, "fontColor": "#000000",
                "background": "#FFFFFF", "cornerRadius": 0 $extra}"""
        val icon = """, "image": "button_icon_probe", "tintColor": "#000000""""
        val cases = listOf(
            "$icon, \"textAlign\": \"Left\"" to "left",
            "$icon, \"textAlign\": \"Right\"" to "right",
            "$icon, \"textAlign\": \"Center\"" to "middle",
            icon to "middle",
        )
        val widths = mutableListOf<Int>()
        val drawn = cases.map { (extra, _) ->
            show(button(extra))
            rule.mainClock.advanceTimeBy(100)
            val (white, black) = extent(columns = true)
            if (white == null || black == null) "not drawn" else {
                val left = black.first - white.first
                val right = white.last - black.last
                if (extra.startsWith(icon)) widths += black.last - black.first
                println("BUTTON_GROUP $extra: ink ${left}..${right} of ${white.last - white.first + 1}")
                when {
                    kotlin.math.abs(left - right) <= 2 -> "middle"
                    left < right -> "left"
                    else -> "right"
                }
            }
        }
        assertEquals(cases.map { it.second }, drawn)
        assertTrue("the icon and the text moved apart: $widths", widths.max() - widths.min() <= 1)
        // A wrap-width icon button keeps its content's width: the Row does not fill there.
        show(button("$icon, \"textAlign\": \"Left\"").replace("\"width\": 200", "\"width\": \"wrapContent\""))
        val (white, _) = extent(columns = true)
        val wrapWidth = white?.let { it.last - it.first + 1 } ?: -1
        assertTrue("a wrap-width icon button is $wrapWidth dp wide", wrapWidth in 1..120)
    }

    /**
     * A loading Button (the undeclared `isLoading` runtime extra): the spinner
     * and the text move together by textAlign too. A loading button is
     * disabled — drawn at half alpha — so the box is white here, the frame is
     * the button node's bounds, and the ink any pixel darker than 0.7.
     */
    @Test
    fun aLoadingButtonsSpinnerAndTextMoveTogetherByTextAlign() {
        rule.mainClock.autoAdvance = false
        boxColor = Color.White
        fun button(align: String) =
            """{"type": "Button", "id": "b", "text": "Go", "width": 200, "height": 44, "fontSize": 14, "fontColor": "#000000",
                "background": "#FFFFFF", "cornerRadius": 0, "isLoading": true, "textAlign": "$align"}"""
        val drawn = listOf("Left", "Right", "Center").map { align ->
            show(button(align))
            rule.mainClock.advanceTimeBy(300)
            val frame = rule.onNodeWithTag("b", useUnmergedTree = true).fetchSemanticsNode()
            val box = rule.onNodeWithTag("box").fetchSemanticsNode()
            val map = rule.onNodeWithTag("box").captureToImage().toPixelMap()
            val left0 = (frame.positionInRoot.x - box.positionInRoot.x).toInt()
            val right0 = left0 + frame.size.width - 1
            var i0 = -1
            var i1 = -1
            for (x in left0..right0) for (y in 0 until map.height) {
                val c = map[x, y]
                if (c.red < 0.7f && c.green < 0.7f && c.blue < 0.7f) { if (i0 < 0) i0 = x; i1 = x }
            }
            val d = rule.density.density
            val left = ((i0 - left0) / d).toInt()
            val right = ((right0 - i1) / d).toInt()
            println("BUTTON_GROUP loading $align: ink ${left}..${right} of ${(frame.size.width / d).toInt()}")
            when {
                i0 < 0 -> "not drawn"
                kotlin.math.abs(left - right) <= 2 -> "middle"
                left < right -> "left"
                else -> "right"
            }
        }
        assertEquals(listOf("left", "right", "middle"), drawn)
    }

    @Test
    fun aButtonsTextSitsAcrossItByTextAlign() {
        fun button(extra: String) =
            """{"type": "Button", "id": "b", "text": "Go", "width": 200, "height": 44, "fontSize": 14, "fontColor": "#000000",
                "background": "#FFFFFF", "cornerRadius": 0 $extra}"""
        val cases = listOf(
            """, "textAlign": "Left"""" to "left",
            """, "textAlign": "Right"""" to "right",
            """, "textAlign": "Center"""" to "middle",
            "" to "middle",
        )
        val drawn = cases.map { (extra, _) ->
            show(button(extra))
            val (white, black) = extent(columns = true)
            if (white == null || black == null) "not drawn" else {
                val left = black.first - white.first
                val right = white.last - black.last
                when {
                    kotlin.math.abs(left - right) <= 2 -> "middle"
                    left < right -> "left"
                    else -> "right"
                }
            }
        }
        assertEquals(cases.map { it.second }, drawn)
    }
}
