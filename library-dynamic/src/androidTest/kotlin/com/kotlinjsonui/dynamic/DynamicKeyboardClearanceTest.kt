package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
 * Android `imePadding()` alone gave the same 0 — a field stops at the
 * viewport edge, and the viewport ended at the IME. Now the viewport ends
 * `keyboardAvoidancePadding` dp above it while the IME is up, and (2.38.0)
 * the library's `Modifier.keyboardAvoidance` scrolls the focused field to
 * that edge itself rather than leaving it to Compose's built-in tracking
 * (a device measured the built-in not doing it, same day).
 *
 * The measurement is in-process: the field's bottom (from its bounds in
 * the root) against the IME's top (root height minus the ime inset),
 * read after focusing the field through the semantics tree. Two paddings,
 * so the number FOLLOWS the attribute, and the absent case, so the default
 * is 20 and not 0.
 *
 * 🚨 The field's bottom is read UNCLIPPED (`positionInRoot + size`). Until
 * 2.40.0 it was `boundsInRoot`, clipped by the ScrollView at the edge being
 * measured, and the follow kept the CARET clear rather than the field: a
 * 48dp field reached past the edge and still read as the padding. The
 * TextField at 96dp and the TextView at 140dp are here because only a
 * change of height separates the caret from the field.
 *
 * ⚠️ Needs a software keyboard: an emulator with a hardware keyboard
 * attached shows no IME and the arm cannot measure (it says so).
 */
@RunWith(AndroidJUnit4::class)
class DynamicKeyboardClearanceTest {

    @get:Rule
    val rule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    private fun layout(padding: Int?, before: Int = 14, field: String = FIELD_48): String {
        val attr = if (padding == null) "" else "\"keyboardAvoidancePadding\": $padding,"
        val filler = (0 until before).joinToString(",") {
            """{"type":"Label","id":"filler_$it","text":"filler $it","height":60}"""
        }
        val after = (0 until 14).joinToString(",") {
            """{"type":"Label","id":"after_$it","text":"after $it","height":60}"""
        }
        return """
            {
              "type": "ScrollView", "id": "form_scroll", "width": "matchParent", "height": "matchParent",
              $attr
              "child": [
                { "type": "View", "orientation": "vertical", "width": "matchParent", "child": [
                  $filler,
                  $field,
                  $after
                ] }
              ]
            }
        """.trimIndent()
    }

    /** (field bottom, ime top) in px, both in the root's coordinate space; the bottom unclipped. */
    private fun measure(padding: Int?, field: String = FIELD_48): Pair<Float, Float> {
        var imeBottomPx = 0
        rule.setContent {
            val density = LocalDensity.current
            imeBottomPx = WindowInsets.ime.getBottom(density)
            DynamicRuntimeScope(emptyMap()) { effectiveData ->
                DynamicView(json = JsonParser.parseString(layout(padding, field = field)).asJsonObject, data = effectiveData)
            }
        }
        rule.onNodeWithTag("probe_field").assertIsDisplayed()
        rule.onNodeWithTag("probe_field").performClick()
        // Let the IME animate in and the bring-into-view scroll settle.
        rule.waitUntil(timeoutMillis = 5_000) { imeBottomPx > 0 }
        Thread.sleep(800)
        rule.waitForIdle()
        val node = rule.onNodeWithTag("probe_field").fetchSemanticsNode()
        val fieldBottom = node.positionInRoot.y + node.size.height   // boundsInRoot is clipped
        val rootBottom = rule.onRoot().fetchSemanticsNode().boundsInRoot.bottom
        val imeTop = rootBottom - imeBottomPx
        println("KEYBOARD_CLEARANCE android padding=$padding fieldBottom=$fieldBottom " +
            "clippedBottom=${node.boundsInRoot.bottom} imeTop=$imeTop " +
            "clearancePx=${imeTop - fieldBottom} ime=$imeBottomPx root=$rootBottom height=${node.size.height}")
        return fieldBottom to imeTop
    }

    private fun clearanceDp(padding: Int?, field: String = FIELD_48): Float {
        val (bottom, top) = measure(padding, field)
        assertTrue("no IME appeared — a hardware keyboard is attached?", top < 100_000f)
        return (top - bottom) / rule.density.density
    }

    // One setContent per test (the rule allows one), so one padding per arm.

    private companion object {
        const val FIELD_48 = """{ "type": "TextField", "id": "probe_field", "hint": "probe", "width": "matchParent", "height": 48 }"""
        const val FIELD_96 = """{ "type": "TextField", "id": "probe_field", "hint": "probe", "width": "matchParent", "height": 96 }"""
        const val TEXTVIEW_140 = """{ "type": "TextView", "id": "probe_field", "hint": "probe", "width": "matchParent", "height": 140 }"""
    }

    @Test
    fun aTallerTextFieldStopsItsBottomAtTheClearanceToo() {
        assertEquals("TextField 96dp, padding 20", 20f, clearanceDp(20, FIELD_96), 3f)
    }

    @Test
    fun aTextViewStopsItsBottomAtTheClearance() {
        assertEquals("TextView 140dp, padding 20", 20f, clearanceDp(20, TEXTVIEW_140), 3f)
    }

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

    /**
     * The follow, through the dynamic ScrollView (2.38.0). The field is
     * focused, then scrolled out of the viewport while the IME is down,
     * then the IME comes back: Compose's own tracking is inert here (it
     * needs the focused child to have been fully visible before the
     * shrink), so what brings the field up is the library's
     * `Modifier.keyboardAvoidance`, which the dynamic ScrollView calls.
     * The control for that claim (the same shape without the modifier
     * staying under the keyboard) is `KeyboardAvoidanceFollowTest` in the
     * library module; the dynamic path has no render without it.
     */
    @Test
    fun aFocusedFieldScrolledOutOfViewIsBroughtUpWhenTheImeReturns() {
        var imeBottomPx = 0
        rule.setContent {
            val density = LocalDensity.current
            imeBottomPx = WindowInsets.ime.getBottom(density)
            DynamicRuntimeScope(emptyMap()) { effectiveData ->
                DynamicView(json = JsonParser.parseString(layout(20, before = 20)).asJsonObject, data = effectiveData)
            }
        }
        // 20 fillers put the field below the fold: bring it in, tap it.
        rule.onNodeWithTag("form_scroll").performScrollToNode(hasTestTag("probe_field"))
        rule.onNodeWithTag("probe_field").assertIsDisplayed().performClick()
        rule.waitUntil(timeoutMillis = 5_000) { imeBottomPx > 0 }
        Thread.sleep(800)
        rule.waitForIdle()
        // IME down, field still focused; scroll so an early filler is at the
        // top — the field is then ~900dp below the viewport's top, hidden.
        rule.runOnUiThread {
            WindowCompat.getInsetsController(rule.activity.window, rule.activity.window.decorView)
                .hide(WindowInsetsCompat.Type.ime())
        }
        rule.waitUntil(timeoutMillis = 5_000) { imeBottomPx == 0 }
        Thread.sleep(500)
        rule.waitForIdle()
        rule.onNodeWithTag("form_scroll").performScrollToNode(hasTestTag("filler_2"))
        rule.waitForIdle()
        val hidden = rule.onNodeWithTag("probe_field").fetchSemanticsNode()
        val viewportBottom = rule.onNodeWithTag("form_scroll").fetchSemanticsNode().boundsInRoot.bottom
        val hiddenBy = (hidden.positionInRoot.y + hidden.size.height - viewportBottom) / rule.density.density
        assertEquals("still focused", true, hidden.config.getOrNull(SemanticsProperties.Focused))
        assertTrue("the field must be below the viewport before the IME returns (by $hiddenBy dp)", hiddenBy > 0f)
        rule.runOnUiThread {
            WindowCompat.getInsetsController(rule.activity.window, rule.activity.window.decorView)
                .show(WindowInsetsCompat.Type.ime())
        }
        rule.waitUntil(timeoutMillis = 5_000) { imeBottomPx > 0 }
        Thread.sleep(1_000)
        rule.waitForIdle()
        val node = rule.onNodeWithTag("probe_field").fetchSemanticsNode()
        val fieldBottom = node.positionInRoot.y + node.size.height   // boundsInRoot is clipped
        val rootBottom = rule.onRoot().fetchSemanticsNode().boundsInRoot.bottom
        val clearance = (rootBottom - imeBottomPx - fieldBottom) / rule.density.density
        println("KEYBOARD_CLEARANCE android follow hiddenBy=$hiddenBy fieldBottom=$fieldBottom clippedBottom=${node.boundsInRoot.bottom} imeTop=${rootBottom - imeBottomPx} clearanceDp=$clearance")
        assertEquals("brought up to the clearance", 20f, clearance, 3f)
    }
}
