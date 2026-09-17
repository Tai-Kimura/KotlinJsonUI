package com.kotlinjsonui.components

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * JVM pin for the one piece of caret geometry that is arithmetic: a
 * self-drawn caret is placed from the select's edge (outside the content
 * padding), so the closed-state text must stop short of the row's end by the
 * caret's footprint minus the inset the padding already gives — never less
 * than nothing.
 */
class SelectBoxCaretReserveTest {

    @Test
    fun theFixtureCaretPushesTheTextPastTheDefaultInset() {
        // SelectBox/caretAttributes__static: 32dp wide, 24dp from the edge,
        // default contentPadding end = 16dp → 40dp of the row is the caret's.
        assertEquals(40.dp, caretTextReserve(caretWidth = 32.dp, rightMargin = 24.dp, endInset = 16.dp))
    }

    @Test
    fun aDefaultGlyphFlushAtTheEdgeStillNeedsRoomBeyondTheInset() {
        // `{}`: 24dp glyph, rightMargin 0. The 16dp inset covers 16 of it.
        assertEquals(8.dp, caretTextReserve(caretWidth = DEFAULT_CARET_GLYPH_DP.dp, rightMargin = 0.dp, endInset = 16.dp))
    }

    @Test
    fun aCaretInsideTheInsetReservesNothing() {
        assertEquals(0.dp, caretTextReserve(caretWidth = 10.dp, rightMargin = 0.dp, endInset = 16.dp))
        assertEquals(0.dp, caretTextReserve(caretWidth = 16.dp, rightMargin = 0.dp, endInset = 16.dp))
    }
}
