package com.kotlinjsonui.components

import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A range's decoration over the Label's own (ticket kjui-label-underline-dropped-with-partial-attributes): the Label's
 * underline / strikethrough stay on every range and a range adds its own, as iOS draws them.
 */
class PartialRangeDecorationTest {

    @Test
    fun `a range that declares no line inherits the Label's`() {
        // null, not TextDecoration.None: None on the span took the Label's underline off the range
        assertNull(rangeDecoration(TextDecoration.Underline, underline = false, strikethrough = false))
        assertNull(rangeDecoration(null, underline = false, strikethrough = false))
    }

    @Test
    fun `a range's own line is added to the Label's`() {
        val both = rangeDecoration(TextDecoration.LineThrough, underline = true, strikethrough = false)!!
        assertTrue(both.contains(TextDecoration.Underline) && both.contains(TextDecoration.LineThrough))
        assertEquals(TextDecoration.Underline, rangeDecoration(null, underline = true, strikethrough = false))
        assertEquals(TextDecoration.Underline, rangeDecoration(TextDecoration.None, underline = true, strikethrough = false))
    }

    @Test
    fun `a whole-text range with an underline under a Label underline is one line, not two`() {
        // the shape a screen used before the fix (the range = the whole text, its own underline): the same drawing
        assertEquals(TextDecoration.Underline, rangeDecoration(TextDecoration.Underline, underline = true, strikethrough = false))
    }
}
