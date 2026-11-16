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
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A shadow's bound colour draws as the same colour written, and a field left
 * out draws as its declared default (ticket
 * 2026-11-07-declared-binding-slots-and-dynamic-shadow): the radius, offsets and
 * opacity beside it were read from data, the colour was read as the text "@{c}",
 * parsed as no colour, and the shadow fell to black.
 */
@RunWith(AndroidJUnit4::class)
class DynamicShadowColorBindingTest {
    @get:Rule val rule = createComposeRule()

    private var json by mutableStateOf("{\"type\": \"View\"}")
    private val data = mapOf<String, Any>("c" to "#0000FF", "r" to 8, "o" to 1.0)

    private fun layout(type: String, color: String, extra: String = ""): String =
        "{\"type\": \"$type\", \"id\": \"p\", \"width\": 60, \"height\": 60, \"background\": \"#FF0000\"$extra, " +
            "\"shadow\": {\"color\": $color, \"radius\": 8, \"offsetX\": 6, \"offsetY\": 6, \"opacity\": 1}}"

    private fun pixels(j: String): IntArray {
        json = j
        rule.waitForIdle()
        val map = rule.onNodeWithTag("root").captureToImage().toPixelMap()
        return map.buffer.copyOf()
    }

    private fun differing(a: IntArray, b: IntArray): Int = a.indices.count { a[it] != b.getOrNull(it) }

    @Test
    fun aBoundShadowColourDrawsAsTheSameColourWritten() {
        rule.setContent {
            Box(Modifier.testTag("root").size(160.dp).background(Color.White).padding(40.dp)) {
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
        for ((type, extra) in listOf("View" to "", "Label" to ", \"text\": \"x\"", "Button" to ", \"text\": \"x\"")) {
            val written = pixels(layout(type, "\"#0000FF\"", extra))
            val bound = pixels(layout(type, "\"@{c}\"", extra))
            val other = pixels(layout(type, "\"#00FF00\"", extra))
            // The comparison sees the colour at all: another written colour draws differently.
            assertNotEquals("$type: a green shadow drew as the blue one", 0, differing(written, other))
            assertEquals("$type: the bound colour drew differently from the same colour written", 0, differing(written, bound))
        }
    }

    private fun shadowOf(fields: String): String =
        "{\"type\": \"View\", \"id\": \"p\", \"width\": 60, \"height\": 60, \"background\": \"#FF0000\"" +
            (if (fields.isEmpty()) "" else ", \"shadow\": {$fields}") + "}"

    /**
     * A field left out takes its declared default (attribute_definitions
     * common.shadow, iOS's): radius 5, offsets 0, and no colour is black at 33%
     * with the opacity not applied. Android drew radius 4 in opaque black.
     */
    @Test
    fun aFieldLeftOutDrawsAsItsDeclaredDefault() {
        rule.setContent {
            Box(Modifier.testTag("root").size(160.dp).background(Color.White).padding(40.dp)) {
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
        val declared = pixels(shadowOf("\"radius\": 5, \"offsetX\": 0, \"offsetY\": 0, \"color\": \"#000000\", \"opacity\": 0.33"))
        val leftOut = pixels(shadowOf("\"opacity\": 0.8"))
        val oldAndroid = pixels(shadowOf("\"radius\": 4, \"color\": \"#000000\""))
        assertNotEquals("the old default drew as the declared one", 0, differing(declared, oldAndroid))
        assertEquals("a shadow with every field left out drew differently from the declared defaults", 0, differing(declared, leftOut))
    }

    @Test
    fun aBoundColourThatDoesNotResolveCastsNoShadow() {
        rule.setContent {
            Box(Modifier.testTag("root").size(160.dp).background(Color.White).padding(40.dp)) {
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
        val none = pixels(shadowOf(""))
        assertEquals("an unset bound colour cast a shadow", 0, differing(none, pixels(shadowOf("\"color\": \"@{nope}\", \"radius\": 8"))))
        assertNotEquals("a written colour cast none", 0, differing(none, pixels(shadowOf("\"color\": \"#0000FF\", \"radius\": 8"))))
    }
}
