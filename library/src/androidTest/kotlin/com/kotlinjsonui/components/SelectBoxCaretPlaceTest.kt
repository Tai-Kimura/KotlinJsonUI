package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * caretAttributes without rightMargin puts the caret where the default arrow sits without the object - the content
 * padding's end, 12 dp by SelectBoxDefaults - so the object's presence never moves it (the requester's ruling
 * 2026-11-14, the three faces alike; ticket jui-selectbox-caret-right-margin-absent-differs-by-face). Android alone
 * read it as 0 and drew the caret flush against the edge, 12 dp right of iOS and the web. The caret's background marks
 * its box; its right edge with the key absent is read against the same caret written `rightMargin: 12`.
 */
@RunWith(AndroidJUnit4::class)
class SelectBoxCaretPlaceTest {
    @get:Rule val rule = createComposeRule()

    private val green = Color(0xFF00C800)

    /** The rightmost column holding the caret's green box, in px of the tagged field. */
    private fun greenRight(tag: String): Int {
        val map = rule.onNodeWithTag(tag).captureToImage().toPixelMap()
        var right = -1
        for (y in 0 until map.height) for (x in 0 until map.width) {
            val c = map[x, y]
            if (c.red < 0.1f && c.green > 0.7f && c.blue < 0.1f && x > right) right = x
        }
        return right
    }

    @Test
    fun an_absent_right_margin_is_the_default_arrows_place() {
        val absent = SelectBoxCaret(width = 16, height = 16, background = green)
        val written = absent.copy(rightMargin = SelectBoxDefaults.contentPadding.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr).value.toInt())
        rule.setContent {
            MaterialTheme {
                Column {
                    Box(Modifier.testTag("list_absent")) {
                        SelectBox(value = "One", onValueChange = {}, options = listOf("One"), modifier = Modifier.width(200.dp), caret = absent)
                    }
                    Box(Modifier.testTag("list_written")) {
                        SelectBox(value = "One", onValueChange = {}, options = listOf("One"), modifier = Modifier.width(200.dp), caret = written)
                    }
                    Box(Modifier.testTag("date_absent")) {
                        DateSelectBox(value = "", onValueChange = {}, modifier = Modifier.width(200.dp), caret = absent)
                    }
                    Box(Modifier.testTag("date_written")) {
                        DateSelectBox(value = "", onValueChange = {}, modifier = Modifier.width(200.dp), caret = written)
                    }
                }
            }
        }
        rule.waitForIdle()
        for (kind in listOf("list", "date")) {
            val written = greenRight("${kind}_written")
            assertTrue("$kind: the written caret is drawn ($written)", written > 0)
            assertEquals("$kind: rightMargin absent sits where the default arrow does", written, greenRight("${kind}_absent"))
        }
    }
}
