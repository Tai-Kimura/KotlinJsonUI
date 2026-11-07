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
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
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

/** A TextField's outline width and a bound secure, through DynamicView. */
@RunWith(AndroidJUnit4::class)
class DynamicTextFieldBorderAndSecureTest {
    @get:Rule val rule = createComposeRule()

    private var json by mutableStateOf("{\"type\": \"View\"}")
    private val data = mapOf<String, Any>("w" to 5, "on" to true, "off" to false, "t" to "secret")

    private fun start() {
        rule.setContent {
            Box(Modifier.testTag("root").size(260.dp, 120.dp).background(Color.White).padding(10.dp)) {
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

    private fun field(attrs: String) =
        "{\"type\": \"TextField\", \"id\": \"f\", \"width\": 200, \"height\": 56, \"borderColor\": \"#FF0000\", \"cornerRadius\": 0$attrs}"

    private fun show(j: String): Int {
        json = j
        rule.waitForIdle()
        return redPixels()
    }

    @Test
    fun theOutlineWidthIsDrawnWrittenOrBound() {
        start()
        val none = show(field(""))
        assertTrue("the outline did not draw", none > 0)
        val written = show(field(", \"borderWidth\": 5"))
        assertTrue("a written width is not thicker: $none / $written", written > none)
        assertEquals("a bound width draws otherwise than the written one", written, show(field(", \"borderWidth\": \"@{w}\"")))
    }

    private val isPassword = SemanticsMatcher.keyIsDefined(SemanticsProperties.Password)

    private fun secure(value: String): Boolean {
        json = "{\"type\": \"TextField\", \"id\": \"s\", \"testTag\": \"s\", \"width\": 200, \"height\": 56, \"text\": \"@{t}\", \"secure\": $value}"
        rule.waitForIdle()
        return rule.onAllNodes(isPassword).fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun aBoundSecureFollowsTheData() {
        start()
        assertTrue("written true does not hide the text", secure("true"))
        assertTrue("bound true does not hide the text", secure("\"@{on}\""))
        assertEquals("bound false hides the text", false, secure("\"@{off}\""))
    }
}
