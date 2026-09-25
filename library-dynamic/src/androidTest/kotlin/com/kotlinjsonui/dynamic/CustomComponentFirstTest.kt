package com.kotlinjsonui.dynamic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A type the app registers as its own component reaches the app's handler
 * before any built-in case, as kjui's codegen takes the app's converter
 * before its own — whatever the type is called: a built-in name (Label), a
 * built-in spelling (ProgressBar, which DynamicView switched to the built-in
 * Progress), a spelling of View in the type-synonym canon (HStack).
 *
 * Measured before the change (CustomComponentShadowProbe, 1595e30): the
 * handler was not called for ProgressBar, WebView or Scroll, and was for a
 * type no built-in case takes. An app's ProgressBar was drawn in Debug as the
 * built-in Progress while its release build drew the app's.
 */
@RunWith(AndroidJUnit4::class)
class CustomComponentFirstTest {
    @get:Rule
    val rule = createComposeRule()

    private var saved: (@Composable (String, JsonObject, Map<String, Any>) -> Boolean)? = null

    @Before
    fun save() { saved = Configuration.customComponentHandler }

    @After
    fun restore() { Configuration.customComponentHandler = saved }

    /** Every type the handler is asked for, and those it took. */
    private fun asked(registered: Set<String>, types: List<String>): Pair<List<String>, List<String>> {
        val all = mutableListOf<String>()
        val asked = mutableListOf<String>()
        Configuration.customComponentHandler = { type, _, _ ->
            all += type
            if (type in registered) {
                asked += type
                Text("app $type")
                true
            } else {
                false
            }
        }
        val json = "{\"type\": \"View\", \"child\": [" + types.joinToString(",") { "{\"type\": \"$it\", \"text\": \"t\"}" } + "]}"
        rule.setContent { DynamicView(json = JsonParser.parseString(json).asJsonObject, data = emptyMap()) }
        rule.waitForIdle()
        return all to asked
    }

    @Test
    fun anAppRegisteredTypeReachesTheAppFirst() {
        val types = listOf("ProgressBar", "Label", "HStack", "ProbeCustomType")
        assertEquals(types, asked(types.toSet(), types).second.distinct())
    }

    @Test
    fun anUnregisteredBuiltInIsStillTheBuiltIn() {
        // The handler answers false for what the app did not register: the
        // built-in draws, and each node is asked once per composition — as
        // often as its parent, the root View, so no second ask comes from the
        // unknown-type branch.
        val types = listOf("Label", "ProgressBar")
        val (all, taken) = asked(emptySet(), types)
        assertEquals(emptyList<String>(), taken)
        val rootAsks = all.count { it == "View" }
        for (t in types) assertEquals("$t asked as often as the root", rootAsks, all.count { it == t })
    }
}
