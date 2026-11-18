package com.kotlinjsonui.dynamic

import com.kotlinjsonui.dynamic.components.DynamicTextComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The font size a Label's lines and autoSize are reckoned from, one reading for the plain Label and the partial /
 * linkable one (Label/highlightAttributes__static_with_partial: the partial path took the Label's own size and drew a
 * selected highlight's 24sp x lineHeightMultiple 1.5 line from the theme's, 36 where the plain Label draws 47).
 */
class LabelLineFontSizeTest {

    @Test
    fun `a highlight's or a hint's font size is the one the lines are reckoned from while it is in force`() {
        assertEquals(24f, DynamicTextComponent.lineFontSize(mapOf("fontSize" to 24, "lineHeightMultiple" to 1.5), null))
        assertEquals(24f, DynamicTextComponent.lineFontSize(mapOf("fontSize" to 24.0), 14f))
    }

    @Test
    fun `without one in force, the Label's own`() {
        assertEquals(14f, DynamicTextComponent.lineFontSize(mapOf("fontColor" to "#FF0000"), 14f))
        assertEquals(14f, DynamicTextComponent.lineFontSize(null, 14f))
        assertNull(DynamicTextComponent.lineFontSize(null, null))
    }
}
