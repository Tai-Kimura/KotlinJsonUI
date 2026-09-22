package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * How far above the IME does a focused field stop, through the dynamic
 * ScrollView with `keyboardAvoidancePadding` (SSoT: number, default 20)?
 *
 * Reported 2026-09-22 (iOS first, from a real device): the focused field
 * sat on the edge below it with no layout attribute to ask for room. On
 * Android `imePadding()` alone gave the same 0 — Compose's bringIntoView
 * stops a focused field at the viewport edge, and the viewport ended at the
 * IME. Now the viewport ends `keyboardAvoidancePadding` dp above it while
 * the IME is up.
 *
 * The measurement is in-process: the field's bottom (from its bounds in
 * the root) against the IME's top (root height minus the ime inset),
 * read after focusing the field through the semantics tree. Two paddings,
 * so the number FOLLOWS the attribute, and the absent case, so the default
 * is 20 and not 0.
 *
 * ⚠️ Needs a software keyboard: an emulator with a hardware keyboard
 * attached shows no IME and the arm cannot measure (it says so).
 */
@RunWith(AndroidJUnit4::class)
class DynamicKeyboardClearanceTest {

    @get:Rule
    val rule = createComposeRule()

    private fun layout(padding: Int?): String {
        val attr = if (padding == null) "" else "\"keyboardAvoidancePadding\": $padding,"
        val filler = (0 until 14).joinToString(",") {
            """{"type":"Label","id":"filler_$it","text":"filler $it","height":60}"""
        }
        return """
            {
              "type": "ScrollView", "id": "form_scroll", "width": "matchParent", "height": "matchParent",
              $attr
              "child": [
                { "type": "View", "orientation": "vertical", "width": "matchParent", "child": [
                  $filler,
                  { "type": "TextField", "id": "probe_field", "hint": "probe", "width": "matchParent", "height": 48 },
                  $filler
                ] }
              ]
            }
        """.trimIndent()
    }

    /** (field bottom, ime top) in px, both in the root's coordinate space. */
    private fun measure(padding: Int?): Pair<Float, Float> {
        var imeBottomPx = 0
        rule.setContent {
            val density = LocalDensity.current
            imeBottomPx = WindowInsets.ime.getBottom(density)
            DynamicRuntimeScope(emptyMap()) { effectiveData ->
                DynamicView(json = JsonParser.parseString(layout(padding)).asJsonObject, data = effectiveData)
            }
        }
        rule.onNodeWithTag("probe_field").assertIsDisplayed()
        rule.onNodeWithTag("probe_field").performClick()
        // Let the IME animate in and the bring-into-view scroll settle.
        rule.waitUntil(timeoutMillis = 5_000) { imeBottomPx > 0 }
        Thread.sleep(800)
        rule.waitForIdle()
        val bounds = rule.onNodeWithTag("probe_field").fetchSemanticsNode().boundsInRoot
        val rootBottom = rule.onRoot().fetchSemanticsNode().boundsInRoot.bottom
        val imeTop = rootBottom - imeBottomPx
        println("KEYBOARD_CLEARANCE android padding=$padding fieldBottom=${bounds.bottom} imeTop=$imeTop " +
            "clearancePx=${imeTop - bounds.bottom} ime=$imeBottomPx root=$rootBottom")
        return bounds.bottom to imeTop
    }

    private fun clearanceDp(padding: Int?): Float {
        val (bottom, top) = measure(padding)
        assertTrue("no IME appeared — a hardware keyboard is attached?", top < 100_000f)
        return (top - bottom) / rule.density.density
    }

    // One setContent per test (the rule allows one), so one padding per arm.

    @Test
    fun aFocusedFieldStopsTheDeclaredClearanceAboveTheIme() {
        assertEquals("padding 20", 20f, clearanceDp(20), 3f)
    }

    @Test
    fun theClearanceFollowsTheAttribute() {
        assertEquals("padding 60", 60f, clearanceDp(60), 3f)
    }

    @Test
    fun anAbsentAttributeIsTheSsotDefaultOfTwentyNotZero() {
        assertEquals("absent → 20", 20f, clearanceDp(null), 3f)
    }
}
