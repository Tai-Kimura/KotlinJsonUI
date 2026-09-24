package com.kotlinjsonui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Does a focused field end up `clearanceDp` above the visible bottom once
 * the IME is up, through `Modifier.keyboardAvoidance`?
 *
 * The form is the codegen's shape for `ScrollView(weight 1)` + a fixed
 * footer, read from a consumer's generated view 2026-09-22: a SafeAreaView
 * root Column (`systemBarsPadding().imePadding()`), the LazyColumn with
 * `weight(1f)`, one lazy item holding the whole form, the footer as the
 * LazyColumn's sibling. The activity is the reporting face's window:
 * `adjustNothing` in the manifest, edge-to-edge in code.
 *
 * "Visible bottom" here is the LazyColumn's own bottom (= the footer's
 * top), which its outer bounds report; the padded viewport ends
 * `clearanceDp` above that, so a field stopping at the viewport's edge
 * reads as `clearanceDp` in [clearanceDp].
 *
 * 🚨 The field's bottom is read UNCLIPPED (`positionInRoot + size`). Until
 * 2.40.0 [clearanceDp] read `boundsInRoot`, which the list clips at exactly
 * the edge being measured: a field reaching 15.6dp past it (the follow kept
 * the caret clear, not the field) read as 20dp, and all four arms were
 * green on the defect. Heights other than 48dp are in
 * KeyboardAvoidanceFieldBottomTest — the caret and the field only give
 * different answers when the height changes.
 *
 * ⚠️ THE CONTROL ARM ASSERTS A DEFECT. `clippedRaw` runs the 2.37.0 emit
 * (viewport half only, follow left to Compose) in the one shape where
 * Compose's own tracking is inert by its documented precondition — the
 * field is focused but not fully visible when the viewport shrinks — and
 * asserts the field is NOT brought up. That is the arm that gives
 * `clippedLibrary` its power: if Compose starts handling this shape, the
 * control goes red and says the discriminator is gone, rather than the
 * library arm passing for a reason that is not the library.
 *
 * ⚠️ Needs a software keyboard on the device. The measured emulator (API
 * 35, 1080x2400, LatinIME) followed in EVERY tap shape through the built-in
 * tracking alone; the user's device did not, and which variable differs
 * there is not identified — see KeyboardAvoidance.kt.
 */
@RunWith(AndroidJUnit4::class)
class KeyboardAvoidanceFollowTest {

    @get:Rule
    val rule = createAndroidComposeRule<AdjustNothingEdgeToEdgeActivity>()

    companion object {
        const val FIELD = "probe_field"
        const val SCROLL = "form_scroll"
        const val CLEARANCE = 20
        const val TOLERANCE_DP = 3f
    }

    @Composable
    private fun Form(listState: LazyListState, fillerCount: Int, useLibrary: Boolean) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White).systemBarsPadding().imePadding()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .testTag(SCROLL)
                    .fillMaxWidth()
                    .weight(1f)
                    .then(
                        if (useLibrary) Modifier.keyboardAvoidance(listState, CLEARANCE)
                        else Modifier // the 2.37.0 emit, verbatim: the viewport half only
                            .imePadding()
                            .padding(bottom = if (WindowInsets.ime.getBottom(LocalDensity.current) > 0) CLEARANCE.dp else 0.dp)
                    )
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        repeat(fillerCount) { i -> Text("filler $i", modifier = Modifier.fillMaxWidth().height(60.dp)) }
                        CustomTextField(
                            state = rememberTextFieldState(),
                            modifier = Modifier.testTag(FIELD).fillMaxWidth().height(48.dp),
                            backgroundColor = Color(0xFFEEEEEE),
                        )
                        repeat(fillerCount) { i -> Text("after $i", modifier = Modifier.fillMaxWidth().height(60.dp)) }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.LightGray))
        }
    }

    private var imePx = 0
    private var listState: LazyListState? = null

    private fun content(fillerCount: Int, useLibrary: Boolean) {
        rule.setContent {
            imePx = WindowInsets.ime.getBottom(LocalDensity.current)
            val st = rememberLazyListState()
            listState = st
            Form(st, fillerCount, useLibrary)
        }
        rule.waitForIdle()
    }

    private fun ime(show: Boolean) {
        rule.runOnUiThread {
            val c = WindowCompat.getInsetsController(rule.activity.window, rule.activity.window.decorView)
            if (show) c.show(WindowInsetsCompat.Type.ime()) else c.hide(WindowInsetsCompat.Type.ime())
        }
        rule.waitUntil(timeoutMillis = 5_000) { if (show) imePx > 0 else imePx == 0 }
        Thread.sleep(if (show) 1_000 else 500)
        rule.waitForIdle()
    }

    /** Field bottom (unclipped) against the LazyColumn's outer bottom, in dp; the IME must be up. */
    private fun clearanceDp(label: String): Float {
        assertTrue("no IME appeared — a hardware keyboard is attached?", imePx > 0)
        val node = rule.onNodeWithTag(FIELD).fetchSemanticsNode()
        val fieldBottom = node.positionInRoot.y + node.size.height   // boundsInRoot is clipped
        val scroll = rule.onNodeWithTag(SCROLL).fetchSemanticsNode().boundsInRoot
        val dp = (scroll.bottom - fieldBottom) / rule.density.density
        println("KEYBOARD_FOLLOW $label fieldBottom=$fieldBottom clippedBottom=${node.boundsInRoot.bottom} " +
            "scrollBottom=${scroll.bottom} ime=$imePx clearanceDp=$dp")
        return dp
    }

    private fun tap() {
        rule.onNodeWithTag(FIELD).performClick()
        rule.waitUntil(timeoutMillis = 5_000) { imePx > 0 }
        Thread.sleep(1_000)
        rule.waitForIdle()
    }

    /**
     * Focus the field (IME up, field placed), hide the IME, scroll the list
     * so the field's bottom sits `clipDp` BELOW the viewport's bottom (a
     * scroll is not a focus change, so nothing brings it back), show the
     * IME again with the field still focused.
     */
    private fun focusThenClip(clipDp: Int = 24) {
        tap()
        ime(show = false)
        val d = rule.density.density
        // Back to the top, where the 13-filler form puts the field's bottom
        // BELOW the viewport's bottom; then forward by the exact amount that
        // leaves `clipDp` of it hidden. Two steps because a backward scroll
        // clamps at the top (the first draft asked for one backward scroll
        // and got a fully visible field, and a control that followed).
        rule.runOnIdle { runBlocking { listState!!.scrollToItem(0, 0) } }
        rule.waitForIdle()
        val scroll = rule.onNodeWithTag(SCROLL).fetchSemanticsNode().boundsInRoot
        val node = rule.onNodeWithTag(FIELD).fetchSemanticsNode()
        val unclippedBottom = node.positionInRoot.y + node.size.height   // boundsInRoot is clipped
        val forward = unclippedBottom - (scroll.bottom + clipDp * d)
        assertTrue("the form must put the field below the fold at the top (bottom=$unclippedBottom, viewport=${scroll.bottom})", forward >= 0f)
        rule.runOnIdle { runBlocking { listState!!.scrollBy(forward) } }
        rule.waitForIdle()
        val after = rule.onNodeWithTag(FIELD).fetchSemanticsNode()
        val clippedBy = (after.positionInRoot.y + after.size.height - scroll.bottom) / d
        val focused = after.config.getOrNull(SemanticsProperties.Focused)
        println("KEYBOARD_FOLLOW clip: hiddenDp=$clippedBy focused=$focused")
        assertEquals("the field must still be focused after the scroll", true, focused)
        assertEquals("the field's bottom must sit clipDp below the viewport", clipDp.toFloat(), clippedBy, 2f)
        ime(show = true)
    }

    @Test
    fun aTappedFieldStopsTheClearanceAboveTheVisibleBottom() {
        content(fillerCount = 12, useLibrary = true)   // fully visible before the tap
        tap()
        assertEquals(CLEARANCE.toFloat(), clearanceDp("tap fully-visible"), TOLERANCE_DP)
    }

    @Test
    fun aTappedFieldThatWasPartlyHiddenStopsThereToo() {
        content(fillerCount = 14, useLibrary = true)
        // Field below the fold: bring it to 24dp of its 48 showing, then tap.
        val d = rule.density.density
        val scroll = rule.onNodeWithTag(SCROLL).fetchSemanticsNode().boundsInRoot
        val fieldBottomInItem = (14 * 60 + 48) * d
        rule.runOnIdle { runBlocking { listState!!.scrollToItem(0, (fieldBottomInItem - scroll.height - 24 * d).toInt()) } }
        rule.waitForIdle()
        tap()
        assertEquals(CLEARANCE.toFloat(), clearanceDp("tap partly-visible"), TOLERANCE_DP)
    }

    @Test
    fun clippedRaw_theControl_compose_alone_leaves_the_field_under_the_keyboard() {
        content(fillerCount = 13, useLibrary = false)
        focusThenClip()
        val field = rule.onNodeWithTag(FIELD).fetchSemanticsNode().boundsInRoot
        val scroll = rule.onNodeWithTag(SCROLL).fetchSemanticsNode().boundsInRoot
        println("KEYBOARD_FOLLOW clipped raw fieldBottom=${field.bottom} scrollBottom=${scroll.bottom} ime=$imePx")
        // Clipped bounds: a field wholly outside the viewport reports an empty rect.
        assertTrue("the control lost its shape: Compose brought the field up on its own (field=$field, scroll=$scroll)",
            field.isEmpty || field.bottom > scroll.bottom - CLEARANCE * rule.density.density + 1f)
    }

    @Test
    fun clippedLibrary_theFollowBringsTheFieldUp() {
        content(fillerCount = 13, useLibrary = true)
        focusThenClip()
        assertEquals(CLEARANCE.toFloat(), clearanceDp("clipped library"), TOLERANCE_DP)
    }
}
