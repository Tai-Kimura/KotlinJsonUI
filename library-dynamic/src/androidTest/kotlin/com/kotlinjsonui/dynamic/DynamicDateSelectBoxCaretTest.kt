package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicSelectBoxComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A Date SelectBox draws its caretAttributes as the list box does (ticket
 * kjui-date-selectbox-caret-attributes-not-emitted): KotlinJsonUI's DateSelectBox takes the same SelectBoxCaret since
 * 2.5.36, SwiftJsonUI's date box and the web date input honour the object, and the Dynamic Date branch never read it
 * - the app's picture never reached the Android date box. The caret's `background` paints the caret's own box, so
 * its colour on the field says whether the object arrived. The list box is the control (it always took it), and a
 * Date box without the object draws no such colour.
 */
@RunWith(AndroidJUnit4::class)
class DynamicDateSelectBoxCaretTest {
    @get:Rule val rule = createComposeRule()

    private val caret = ""","caretAttributes":{"width":32,"height":32,"background":"#00AA00","rightMargin":24}"""

    private fun node(kind: String, extra: String) = JsonParser.parseString(
        when (kind) {
            "list" -> """{"type":"SelectBox","width":200,"items":["One"],"selectedIndex":0$extra}"""
            else -> """{"type":"SelectBox","width":200,"selectItemType":"Date","hint":"Pick a day"$extra}"""
        }
    ).asJsonObject

    /** The pixels of the caret's background colour (#00AA00, within a little of it) on the tagged field. */
    private fun greenPixels(tag: String): Int {
        val map = rule.onNodeWithTag(tag).captureToImage().toPixelMap()
        var n = 0
        for (y in 0 until map.height) for (x in 0 until map.width) {
            val c = map[x, y]
            if (c.red < 0.1f && c.green > 0.6f && c.green < 0.73f && c.blue < 0.1f) n++
        }
        return n
    }

    @Test
    fun a_date_select_draws_its_caret_attributes_as_the_list_box_does() {
        rule.setContent {
            MaterialTheme {
                Column {
                    Box(Modifier.testTag("list_caret")) { DynamicSelectBoxComponent.create(node("list", caret)) }
                    Box(Modifier.testTag("date_caret")) { DynamicSelectBoxComponent.create(node("date", caret)) }
                    Box(Modifier.testTag("date_plain")) { DynamicSelectBoxComponent.create(node("date", "")) }
                }
            }
        }
        rule.waitForIdle()
        val list = greenPixels("list_caret")
        val date = greenPixels("date_caret")
        val plain = greenPixels("date_plain")
        // the control: the list box paints the caret's 32x32 box
        assertTrue("list box paints the caret background: $list px", list > 200)
        assertTrue("date box paints the caret background as the list box does: $date vs $list px", date > list / 2)
        assertEquals("a date box without caretAttributes paints no caret background", 0, plain)
    }
}
