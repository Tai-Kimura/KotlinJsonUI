package com.kotlinjsonui.components

import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * JVM pin for the follow's arithmetic: how far to scroll, given the rect
 * `getFocusedRect()` returns (the caret, for a text field), the focused
 * field's bounds, and the viewport's height — all in the viewport's
 * coordinates.
 *
 * The device arms are KeyboardAvoidanceFieldBottomTest / FollowTest and the
 * dynamic DynamicKeyboardClearanceTest; they need an emulator with a
 * software keyboard, which this repository's CI does not run. This is the
 * part CI does run.
 */
class KeyboardAvoidanceShortfallTest {

    private val viewport = 1000f
    private fun caret(top: Float) = Rect(40f, top, 45f, top + 43f)

    @Test
    fun withoutAFieldItIsTheCaretsShortfall() {
        // A focused thing with no keyboardAvoidanceField: the 2.39.0 answer.
        assertEquals(76f, shortfall(caret(1033f), field = null, viewportHeight = viewport), 0f)
    }

    @Test
    fun withAFieldItIsTheFieldsShortfall() {
        // A 96dp-at-2.625 field (252px) whose caret sits mid-field: the caret
        // alone would stop 105px short of bringing the field's bottom in.
        val field = Rect(0f, 900f, 1080f, 1152f)
        assertEquals(152f, shortfall(caret(1005f), field, viewport), 0f)
        assertEquals("the caret's answer, for contrast", 48f, shortfall(caret(1005f), null, viewport), 0f)
    }

    @Test
    fun aFieldTallerThanTheViewportStopsWithTheCaretAtTheTop() {
        // 2363px field, caret 42px below its top, which is 400px down: the
        // field's bottom would need 1763px, the caret's top leaves at 442.
        val field = Rect(0f, 400f, 1080f, 2763f)
        assertEquals(442f, shortfall(caret(442f), field, viewport), 0f)
    }

    @Test
    fun theLimitIsTheCaretsTopOnlyWhenItIsSmallerThanTheFieldsShortfall() {
        // Boundary: field shortfall == caret top — both give the same answer.
        val field = Rect(0f, 500f, 1080f, 1400f)
        assertEquals(400f, shortfall(caret(400f), field, viewport), 0f)
        // One pixel lower caret: the field's shortfall is now the smaller.
        assertEquals(400f, shortfall(caret(401f), field, viewport), 0f)
        // One pixel higher: the caret's top is.
        assertEquals(399f, shortfall(caret(399f), field, viewport), 0f)
    }

    @Test
    fun neverLessThanBringsTheFocusedRectItselfIn() {
        // A selection rect taller than the viewport (getFocusedRect returns
        // the selection while one is active): the field's answer and the
        // caret-top limit would both leave its bottom below the edge.
        val selection = Rect(0f, 100f, 1080f, 1300f)
        val field = Rect(0f, 50f, 1080f, 1350f)
        assertEquals(300f, shortfall(selection, field, viewport), 0f)
    }

    @Test
    fun aFieldAlreadyClearAsksForNothingPositive() {
        val field = Rect(0f, 300f, 1080f, 426f)
        assertEquals(-574f, shortfall(caret(342f), field, viewport), 0f)
    }
}
