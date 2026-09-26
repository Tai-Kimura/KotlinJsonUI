package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * An Indicator draws its declared `indicatorStyle`: small 16dp, large 48dp
 * (kjui_tools STYLE_SIZES), medium the spinner's own round size, linear a bar.
 * The legacy `style` is not read — a layout the normalizer did not fold draws
 * medium (jsonui-cli ea985526; IndicatorStyleTest is the JVM half).
 */
@RunWith(AndroidJUnit4::class)
class IndicatorStyleDrawsTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun eachStyleDrawsItsShape() {
        val nodes = listOf("small", "medium", "large", "linear").joinToString(",") {
            "{\"type\": \"Indicator\", \"id\": \"ind_$it\", \"indicatorStyle\": \"$it\"}"
        } + ",{\"type\": \"Indicator\", \"id\": \"ind_legacy\", \"style\": \"large\"}"
        val json = JsonParser.parseString("{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [$nodes]}").asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        rule.waitForIdle()

        fun size(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()
            .let { (it.right - it.left) to (it.bottom - it.top) }

        assertEquals(16.dp to 16.dp, size("ind_small"))
        assertEquals(48.dp to 48.dp, size("ind_large"))
        val (mw, mh) = size("ind_medium")
        assertEquals("medium is round", mw, mh)
        val (lw, lh) = size("ind_linear")
        assertTrue("linear is a bar ($lw x $lh)", lw > lh)
        assertEquals("an unfolded legacy `style` draws medium", size("ind_medium"), size("ind_legacy"))
    }
}
