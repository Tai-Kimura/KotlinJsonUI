package com.kotlinjsonui.views

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for safe area inset position parsing logic
 * Tests the logic used by KjuiSafeAreaView.parseSafeAreaInsetPositions
 */
class SafeAreaInsetPositionParserTest {

    @Test
    fun `parse single position top`() {
        val result = parseSafeAreaInsetPositions("top")
        assertTrue(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse single position bottom`() {
        val result = parseSafeAreaInsetPositions("bottom")
        assertFalse(result.applyTopInset)
        assertTrue(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse single position left`() {
        val result = parseSafeAreaInsetPositions("left")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertTrue(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse single position right`() {
        val result = parseSafeAreaInsetPositions("right")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertTrue(result.applyRightInset)
    }

    @Test
    fun `parse pipe separated positions`() {
        val result = parseSafeAreaInsetPositions("top|bottom")
        assertTrue(result.applyTopInset)
        assertTrue(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse comma separated positions`() {
        val result = parseSafeAreaInsetPositions("top,bottom")
        assertTrue(result.applyTopInset)
        assertTrue(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse vertical position`() {
        val result = parseSafeAreaInsetPositions("vertical")
        assertTrue(result.applyTopInset)
        assertTrue(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse horizontal position`() {
        val result = parseSafeAreaInsetPositions("horizontal")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertTrue(result.applyLeftInset)
        assertTrue(result.applyRightInset)
        assertTrue(result.applyStartInset)
        assertTrue(result.applyEndInset)
    }

    @Test
    fun `parse all position`() {
        val result = parseSafeAreaInsetPositions("all")
        assertTrue(result.applyTopInset)
        assertTrue(result.applyBottomInset)
        assertTrue(result.applyLeftInset)
        assertTrue(result.applyRightInset)
        assertTrue(result.applyStartInset)
        assertTrue(result.applyEndInset)
    }

    @Test
    fun `parse start position`() {
        val result = parseSafeAreaInsetPositions("start")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
        assertTrue(result.applyStartInset)
        assertFalse(result.applyEndInset)
    }

    @Test
    fun `parse end position`() {
        val result = parseSafeAreaInsetPositions("end")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
        assertFalse(result.applyStartInset)
        assertTrue(result.applyEndInset)
    }

    @Test
    fun `parse leading position (alias for start)`() {
        val result = parseSafeAreaInsetPositions("leading")
        assertTrue(result.applyStartInset)
    }

    @Test
    fun `parse trailing position (alias for end)`() {
        val result = parseSafeAreaInsetPositions("trailing")
        assertTrue(result.applyEndInset)
    }

    @Test
    fun `parse case insensitive`() {
        val result = parseSafeAreaInsetPositions("TOP|BOTTOM")
        assertTrue(result.applyTopInset)
        assertTrue(result.applyBottomInset)
    }

    @Test
    fun `parse with whitespace`() {
        val result = parseSafeAreaInsetPositions("  top  |  bottom  ")
        assertTrue(result.applyTopInset)
        assertTrue(result.applyBottomInset)
    }

    @Test
    fun `parse multiple mixed positions`() {
        val result = parseSafeAreaInsetPositions("top,left|right")
        assertTrue(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertTrue(result.applyLeftInset)
        assertTrue(result.applyRightInset)
    }

    @Test
    fun `parse unknown position is ignored`() {
        val result = parseSafeAreaInsetPositions("unknown")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `parse empty string`() {
        val result = parseSafeAreaInsetPositions("")
        assertFalse(result.applyTopInset)
        assertFalse(result.applyBottomInset)
        assertFalse(result.applyLeftInset)
        assertFalse(result.applyRightInset)
    }

    @Test
    fun `setInsetEnabled top enables top`() {
        val insets = InsetFlags()
        setInsetEnabled(insets, "top", true)
        assertTrue(insets.applyTopInset)
    }

    @Test
    fun `setInsetEnabled vertical enables top and bottom`() {
        val insets = InsetFlags()
        setInsetEnabled(insets, "vertical", true)
        assertTrue(insets.applyTopInset)
        assertTrue(insets.applyBottomInset)
    }

    @Test
    fun `setInsetEnabled horizontal enables all horizontal`() {
        val insets = InsetFlags()
        setInsetEnabled(insets, "horizontal", true)
        assertTrue(insets.applyLeftInset)
        assertTrue(insets.applyRightInset)
        assertTrue(insets.applyStartInset)
        assertTrue(insets.applyEndInset)
    }

    @Test
    fun `setInsetEnabled with false disables`() {
        val insets = InsetFlags().apply {
            applyTopInset = true
            applyBottomInset = true
        }
        setInsetEnabled(insets, "top", false)
        assertFalse(insets.applyTopInset)
        assertTrue(insets.applyBottomInset)
    }

    // Data class to hold inset flags for testing
    data class InsetFlags(
        var applyTopInset: Boolean = false,
        var applyBottomInset: Boolean = false,
        var applyLeftInset: Boolean = false,
        var applyRightInset: Boolean = false,
        var applyStartInset: Boolean = false,
        var applyEndInset: Boolean = false
    )

    // Helper function that mirrors KjuiSafeAreaView.parseSafeAreaInsetPositions
    private fun parseSafeAreaInsetPositions(positionsString: String): InsetFlags {
        val flags = InsetFlags()

        val positions = positionsString.split("|", ",").map { it.trim().lowercase() }.toSet()

        for (position in positions) {
            when (position) {
                "top" -> flags.applyTopInset = true
                "bottom" -> flags.applyBottomInset = true
                "left" -> flags.applyLeftInset = true
                "right" -> flags.applyRightInset = true
                "start", "leading" -> flags.applyStartInset = true
                "end", "trailing" -> flags.applyEndInset = true
                "vertical" -> {
                    flags.applyTopInset = true
                    flags.applyBottomInset = true
                }
                "horizontal" -> {
                    flags.applyLeftInset = true
                    flags.applyRightInset = true
                    flags.applyStartInset = true
                    flags.applyEndInset = true
                }
                "all" -> {
                    flags.applyTopInset = true
                    flags.applyBottomInset = true
                    flags.applyLeftInset = true
                    flags.applyRightInset = true
                    flags.applyStartInset = true
                    flags.applyEndInset = true
                }
            }
        }

        return flags
    }

    // Helper function that mirrors KjuiSafeAreaView.setInsetEnabled
    private fun setInsetEnabled(flags: InsetFlags, position: String, enabled: Boolean) {
        when (position.lowercase()) {
            "top" -> flags.applyTopInset = enabled
            "bottom" -> flags.applyBottomInset = enabled
            "left" -> flags.applyLeftInset = enabled
            "right" -> flags.applyRightInset = enabled
            "start", "leading" -> flags.applyStartInset = enabled
            "end", "trailing" -> flags.applyEndInset = enabled
            "vertical" -> {
                flags.applyTopInset = enabled
                flags.applyBottomInset = enabled
            }
            "horizontal" -> {
                flags.applyLeftInset = enabled
                flags.applyRightInset = enabled
                flags.applyStartInset = enabled
                flags.applyEndInset = enabled
            }
        }
    }
}
