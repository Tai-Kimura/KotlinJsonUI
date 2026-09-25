package com.kotlinjsonui.dynamic

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import org.junit.After
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Whether a type an app registers as its own component reaches
 * Configuration.customComponentHandler, or a built-in case takes it first —
 * NOT a test of the suite, and skipped unless requested
 * (`-e customShadowProbe 1`).
 *
 * An app registers its dynamic components through customComponentHandler
 * (a generated DynamicComponentRegistry). DynamicView calls it only for a
 * type none of its own cases takes. Printed per type: SHADOW <type>
 * handler_called=<true|false>.
 */
@RunWith(AndroidJUnit4::class)
class CustomComponentShadowProbe {
    @get:Rule
    val rule = createComposeRule()

    private var saved: Any? = null

    @Before
    fun setUp() {
        val on = InstrumentationRegistry.getArguments().getString("customShadowProbe") == "1"
        Assume.assumeTrue("set -e customShadowProbe 1", on)
        saved = Configuration.customComponentHandler
    }

    @After
    fun tearDown() {
        @Suppress("UNCHECKED_CAST")
        Configuration.customComponentHandler = saved as? (@androidx.compose.runtime.Composable (String, com.google.gson.JsonObject, Map<String, Any>) -> Boolean)
    }

    @Test
    fun whichRegisteredTypesReachTheHandler() {
        val asked = mutableListOf<String>()
        Configuration.customComponentHandler = { type, _, _ ->
            asked += type
            Text("custom $type")
            true
        }
        // ProgressBar / WebView / Scroll are spellings apps use; ProbeCustomType
        // is a type no built-in case takes (the control: it must reach the
        // handler).
        val types = listOf("ProgressBar", "WebView", "Scroll", "ProbeCustomType")
        val json = "{\"type\": \"View\", \"child\": [" + types.joinToString(",") {
            if (it == "WebView") "{\"type\": \"$it\", \"url\": \"data:text/html,probe\"}" else "{\"type\": \"$it\"}"
        } + "]}"
        rule.setContent { DynamicView(json = JsonParser.parseString(json).asJsonObject, data = emptyMap()) }
        rule.waitForIdle()
        for (t in types) println("SHADOW $t handler_called=${t in asked}")
    }
}
