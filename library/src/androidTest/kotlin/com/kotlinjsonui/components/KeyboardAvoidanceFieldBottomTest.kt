package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusTargetModifierNode
import androidx.compose.ui.focus.Focusability
import androidx.compose.ui.focus.getFocusedRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Does the follow keep the FIELD's bottom `clearanceDp` above the visible
 * bottom — or only the caret's?
 *
 * Until 2.40.0 it was the caret: the follow read
 * `FocusTargetModifierNode.getFocusedRect()`, which `BasicTextField` answers
 * with its cursor rect while focused. A caret-anchored follow leaves a field
 * `(height − caret height) / 2` past the edge, so the two readings only part
 * when the height changes — hence 48, 96 and a multi-line 140dp here, where
 * a field-anchored follow gives 20dp for all three. Found by a consumer and
 * checked independently on 2026-09-25 (2.39.0, API 35: 48dp → 4.6dp,
 * 96dp → −19.4dp, the rect the follow read was 16.4dp tall in both); the two
 * CustomTextField arms here are that check, kept.
 *
 * Three instruments on one frame, all unclipped:
 *  - the field's bottom: `positionInRoot + size` of the tagged field
 *    (`boundsInRoot` is clipped at the very edge being measured);
 *  - the rect the follow reads for the caret: `getFocusedRect()` on a
 *    never-focusable target at the root ([RootFocusedRect]);
 *  - the visible bottom: the LazyColumn's outer bottom (= the footer's top).
 *
 * The plain-BasicTextField arms say what a field outside the library gets:
 * the caret, as before, unless it applies `keyboardAvoidanceField()`.
 *
 * ⚠️ Needs a software keyboard on the device.
 */
@RunWith(AndroidJUnit4::class)
class KeyboardAvoidanceFieldBottomTest {

    @get:Rule
    val rule = createAndroidComposeRule<AdjustNothingEdgeToEdgeActivity>()

    /** `getFocusedRect()` of whatever has focus, in root coordinates. */
    internal class RootFocusedRect : DelegatingNode(), LayoutAwareModifierNode {
        private val target = delegate(FocusTargetModifierNode(focusability = Focusability.Never))
        private var coords: LayoutCoordinates? = null
        override fun onPlaced(coordinates: LayoutCoordinates) { coords = coordinates }
        fun inRoot(): Rect? {
            val r = target.getFocusedRect() ?: return null
            val c = coords ?: return null
            return r.translate(c.positionInRoot())
        }
    }

    private class RootFocusedRectElement : ModifierNodeElement<RootFocusedRect>() {
        override fun create() = RootFocusedRect().also { root = it }
        override fun update(node: RootFocusedRect) { root = node }
        override fun equals(other: Any?) = other is RootFocusedRectElement
        override fun hashCode() = 7
    }

    enum class Field { CUSTOM, CUSTOM_MULTILINE, PLAIN, PLAIN_OPTED_IN }

    companion object {
        const val FIELD = "probe_field"
        const val SCROLL = "form_scroll"
        const val CLEARANCE = 20
        const val TOLERANCE_DP = 3f
        internal var root: RootFocusedRect? = null
    }

    private var imePx = 0

    @Composable
    private fun FieldUnderTest(kind: Field, heightDp: Int) {
        val sized = Modifier.testTag(FIELD).fillMaxWidth().height(heightDp.dp)
        when (kind) {
            Field.CUSTOM -> CustomTextField(
                state = rememberTextFieldState(), modifier = sized, backgroundColor = Color(0xFFEEEEEE))
            Field.CUSTOM_MULTILINE -> CustomTextField(
                state = rememberTextFieldState(), modifier = sized, backgroundColor = Color(0xFFEEEEEE),
                singleLine = false)
            Field.PLAIN -> BasicTextField(
                state = rememberTextFieldState(), modifier = sized.background(Color(0xFFEEEEEE)))
            Field.PLAIN_OPTED_IN -> BasicTextField(
                state = rememberTextFieldState(),
                modifier = sized.keyboardAvoidanceField().background(Color(0xFFEEEEEE)))
        }
    }

    /** One frame of the three instruments, once the IME is up and the follow has run. */
    private class Reading(
        val fieldClearanceDp: Float,
        val caretClearanceDp: Float,
        val caretTopBelowViewportTopDp: Float,
        val fieldHeightDp: Float,
        val caretHeightDp: Float,
    )

    /** The list's height with the IME down, in dp — measured once, on the first layout. */
    private var viewportDp by mutableStateOf(0f)
    private var fillers by mutableStateOf(-1)

    /**
     * [fillerCount] null puts the field's top at least 40dp above the fold
     * of this device's list ([fillersToTheFold]: 13 on phone_ci, 11 on
     * conf_ci; the literal was 12) — so it is on screen for the tap and under
     * the IME after it, on a landscape tablet too.
     */
    private fun measure(kind: Field, heightDp: Int, fillerCount: Int? = null): Reading {
        rule.setContent {
            imePx = WindowInsets.ime.getBottom(LocalDensity.current)
            val density = LocalDensity.current.density
            val listState = rememberLazyListState()
            Column(
                modifier = Modifier.fillMaxSize().then(RootFocusedRectElement())
                    .background(Color.White).systemBarsPadding().imePadding()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.testTag(SCROLL).fillMaxWidth().weight(1f)
                        .onSizeChanged { if (viewportDp == 0f) viewportDp = it.height / density }
                        .keyboardAvoidance(listState, CLEARANCE)
                ) {
                    item {
                        if (fillers >= 0) Column(modifier = Modifier.fillMaxWidth()) {
                            repeat(fillers) { i -> Text("filler $i", modifier = Modifier.fillMaxWidth().height(60.dp)) }
                            FieldUnderTest(kind, heightDp)
                            repeat(12) { i -> Text("after $i", modifier = Modifier.fillMaxWidth().height(60.dp)) }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.LightGray))
            }
        }
        rule.waitForIdle()
        assertTrue("the list was never measured", viewportDp > 0f)
        rule.runOnIdle { fillers = fillerCount ?: fillersToTheFold(viewportDp) }
        rule.waitForIdle()
        rule.onNodeWithTag(FIELD).performClick()
        rule.waitUntil(timeoutMillis = 5_000) { imePx > 0 }
        Thread.sleep(1_500)
        rule.waitForIdle()
        assertTrue("no IME appeared — a hardware keyboard is attached?", imePx > 0)

        val d = rule.density.density
        val node = rule.onNodeWithTag(FIELD).fetchSemanticsNode()
        val scroll = rule.onNodeWithTag(SCROLL).fetchSemanticsNode().boundsInRoot
        val fieldBottom = node.positionInRoot.y + node.size.height
        val caret: Rect? = rule.runOnIdle { root?.inRoot() }
        assertNotNull("nothing has focus under the root", caret)
        val r = Reading(
            fieldClearanceDp = (scroll.bottom - fieldBottom) / d,
            caretClearanceDp = (scroll.bottom - caret!!.bottom) / d,
            caretTopBelowViewportTopDp = (caret.top - scroll.top) / d,
            fieldHeightDp = node.size.height / d,
            caretHeightDp = caret.height / d,
        )
        println(
            "KBAV_FIELD kind=$kind h=${heightDp}dp viewportDp=$viewportDp fillers=$fillers ime=$imePx scroll=[${scroll.top}..${scroll.bottom}] " +
                "field=[${node.positionInRoot.y}..$fieldBottom] clippedBottom=${node.boundsInRoot.bottom} caret=$caret " +
                "fieldClearanceDp=${r.fieldClearanceDp} caretClearanceDp=${r.caretClearanceDp} " +
                "caretTopDp=${r.caretTopBelowViewportTopDp} caretHeightDp=${r.caretHeightDp}"
        )
        return r
    }

    @Test
    fun customField48_theFieldsBottomStopsAtTheClearance() {
        val r = measure(Field.CUSTOM, 48)
        assertEquals("field bottom vs visible bottom (dp)", CLEARANCE.toFloat(), r.fieldClearanceDp, TOLERANCE_DP)
    }

    @Test
    fun customField96_theFieldsBottomStopsAtTheClearance() {
        val r = measure(Field.CUSTOM, 96)
        assertEquals("field bottom vs visible bottom (dp)", CLEARANCE.toFloat(), r.fieldClearanceDp, TOLERANCE_DP)
    }

    /** A JsonUI TextView is a multi-line `CustomTextField`. */
    @Test
    fun customMultiLineField140_theFieldsBottomStopsAtTheClearance() {
        val r = measure(Field.CUSTOM_MULTILINE, 140)
        assertEquals("field bottom vs visible bottom (dp)", CLEARANCE.toFloat(), r.fieldClearanceDp, TOLERANCE_DP)
    }

    /**
     * Taller than any viewport the IME leaves: the whole field cannot be
     * shown, and scrolling its bottom up to the edge would push the caret
     * (at the top of an empty field) out of the top. The follow stops where
     * the caret's top meets the viewport's top.
     *
     * Not a control for the fix — the caret-anchored follow passes it too.
     * It guards the fix: a follow that aligned the field's bottom
     * unconditionally fails it.
     */
    @Test
    fun aFieldTallerThanTheViewportKeepsTheCaretInView() {
        val r = measure(Field.CUSTOM_MULTILINE, 900, fillerCount = 2)
        assertTrue("the caret's top left the viewport's top (${r.caretTopBelowViewportTopDp}dp)",
            r.caretTopBelowViewportTopDp >= -1f)
        assertTrue("the caret's bottom is under the clearance band (${r.caretClearanceDp}dp)",
            r.caretClearanceDp >= CLEARANCE - TOLERANCE_DP)
        // The shape: the field really is too tall to fit, so the limit was reached.
        assertTrue("the field fit after all (clearance ${r.fieldClearanceDp}dp) — the arm lost its shape",
            r.fieldClearanceDp < 0f)
    }

    /** What a field outside the library gets: the caret kept clear, as before 2.40.0. */
    @Test
    fun aPlainBasicTextFieldWithoutTheModifierKeepsItsCaretClear() {
        val r = measure(Field.PLAIN, 96)
        assertEquals("caret bottom vs visible bottom (dp)", CLEARANCE.toFloat(), r.caretClearanceDp, TOLERANCE_DP)
        // A bare BasicTextField puts its text at the top, so the field reaches
        // past the edge by most of its height (not the half a vertically
        // centred decoration gives).
        assertTrue("the field's bottom, not the caret's, stopped at the edge (${r.fieldClearanceDp}dp)",
            r.fieldClearanceDp < CLEARANCE - 10f)
    }

    @Test
    fun aPlainBasicTextFieldWithTheModifierKeepsItsBottomClear() {
        val r = measure(Field.PLAIN_OPTED_IN, 96)
        assertEquals("field bottom vs visible bottom (dp)", CLEARANCE.toFloat(), r.fieldClearanceDp, TOLERANCE_DP)
    }
}
