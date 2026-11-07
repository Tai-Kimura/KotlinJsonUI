package com.kotlinjsonui.dynamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** A TextView's outline width through DynamicView, on both of its drawn paths: with margins and without. */
@RunWith(AndroidJUnit4::class)
class DynamicTextViewBorderTest {
    @get:Rule val rule = createComposeRule()

    private var json by mutableStateOf("{\"type\": \"View\"}")
    private val data = mapOf<String, Any>("w" to 5, "t" to "note")

    private fun start() {
        rule.setContent {
            Box(Modifier.testTag("root").size(280.dp, 170.dp).background(Color.White).padding(10.dp)) {
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
    }

    private fun redPixels(): Int {
        val px = rule.onNodeWithTag("root").captureToImage().toPixelMap()
        var n = 0
        for (x in 0 until px.width) for (y in 0 until px.height) {
            val c = px[x, y]
            if (c.red > 0.8f && c.green < 0.4f && c.blue < 0.4f) n++
        }
        return n
    }

    private fun show(margins: String, attrs: String): Int {
        json = "{\"type\": \"TextView\", \"id\": \"f\", \"text\": \"@{t}\", \"width\": 200, \"height\": 100, " +
            "\"background\": \"#FFFFFF\", \"borderColor\": \"#FF0000\", \"cornerRadius\": 0$margins$attrs}"
        rule.waitForIdle()
        return redPixels()
    }

    private fun theWidthIsDrawn(margins: String) {
        val none = show(margins, "")
        assertTrue("the outline did not draw", none > 0)
        assertEquals("a declared 1 changed what is drawn", none, show(margins, ", \"borderWidth\": 1"))
        val written = show(margins, ", \"borderWidth\": 5")
        assertTrue("a written width is not thicker: $none / $written", written > none)
        assertEquals("a bound width draws otherwise than the written one", written, show(margins, ", \"borderWidth\": \"@{w}\""))
        assertEquals("a declared 0 draws a line", 0, show(margins, ", \"borderWidth\": 0"))
    }

    @Test
    fun withoutMargins() {
        start()
        theWidthIsDrawn("")
    }

    @Test
    fun withMargins() {
        start()
        theWidthIsDrawn(", \"topMargin\": 4, \"leftMargin\": 4")
    }
}
