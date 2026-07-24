package com.kotlinjsonui.components

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for visibility logic used in VisibilityWrapper
 * Note: Composable tests require instrumented tests with Compose testing library
 * These tests verify the underlying logic
 */
class VisibilityLogicTest {

    @Test
    fun `visibility gone should not render`() {
        val visibility = "gone"
        assertFalse(shouldRender(visibility))
    }

    @Test
    fun `visibility GONE case insensitive should not render`() {
        val visibility = "GONE"
        assertFalse(shouldRender(visibility))
    }

    @Test
    fun `visibility invisible should render with alpha 0`() {
        val visibility = "invisible"
        assertTrue(shouldRender(visibility))
        assertEquals(0f, getAlpha(visibility), 0.01f)
    }

    @Test
    fun `visibility INVISIBLE case insensitive should render with alpha 0`() {
        val visibility = "INVISIBLE"
        assertTrue(shouldRender(visibility))
        assertEquals(0f, getAlpha(visibility), 0.01f)
    }

    @Test
    fun `visibility visible should render with alpha 1`() {
        val visibility = "visible"
        assertTrue(shouldRender(visibility))
        assertEquals(1f, getAlpha(visibility), 0.01f)
    }

    @Test
    fun `visibility VISIBLE case insensitive should render with alpha 1`() {
        val visibility = "VISIBLE"
        assertTrue(shouldRender(visibility))
        assertEquals(1f, getAlpha(visibility), 0.01f)
    }

    @Test
    fun `visibility unknown defaults to visible`() {
        val visibility = "unknown"
        assertTrue(shouldRender(visibility))
        assertEquals(1f, getAlpha(visibility), 0.01f)
    }

    // hidden: true = boolean shorthand for visibility:"invisible" —
    // the component composes and KEEPS its layout space (must NOT
    // collapse), draws nothing (alpha 0), and clears its semantics so
    // it is invisible to accessibility / UI test drivers.

    @Test
    fun `hidden true composes and keeps layout space`() {
        assertTrue(composes(VisibilityResult.HIDDEN))
        assertTrue(keepsLayoutSpace(VisibilityResult.HIDDEN))
    }

    @Test
    fun `hidden true draws nothing`() {
        assertEquals(0f, alphaOf(VisibilityResult.HIDDEN), 0.01f)
    }

    @Test
    fun `hidden true has no semantics`() {
        assertFalse(hasSemantics(VisibilityResult.HIDDEN))
    }

    @Test
    fun `hidden false renders normally`() {
        val result = computeVisibility(hidden = false, visibility = null)
        assertEquals(VisibilityResult.VISIBLE, result)
        assertTrue(hasSemantics(result))
        assertEquals(1f, alphaOf(result), 0.01f)
    }

    @Test
    fun `hidden null renders normally`() {
        val result = computeVisibility(hidden = null, visibility = null)
        assertEquals(VisibilityResult.VISIBLE, result)
    }

    @Test
    fun `invisible keeps semantics while hidden clears them`() {
        // visibility:"invisible" only skips drawing (alpha 0) — the node
        // stays in the semantics tree. hidden goes further and also
        // clears semantics.
        assertTrue(hasSemantics(VisibilityResult.INVISIBLE))
        assertFalse(hasSemantics(VisibilityResult.HIDDEN))
        assertEquals(0f, alphaOf(VisibilityResult.INVISIBLE), 0.01f)
        assertEquals(0f, alphaOf(VisibilityResult.HIDDEN), 0.01f)
    }

    @Test
    fun `only gone collapses`() {
        assertFalse(composes(VisibilityResult.GONE))
        assertTrue(composes(VisibilityResult.HIDDEN))
        assertTrue(composes(VisibilityResult.INVISIBLE))
        assertTrue(composes(VisibilityResult.VISIBLE))
    }

    @Test
    fun `hidden takes precedence over visibility`() {
        // When hidden=true, visibility should be ignored
        val result = computeVisibility(hidden = true, visibility = "visible")
        assertEquals(VisibilityResult.HIDDEN, result)
    }

    @Test
    fun `visibility gone when not hidden`() {
        val result = computeVisibility(hidden = false, visibility = "gone")
        assertEquals(VisibilityResult.GONE, result)
    }

    @Test
    fun `visibility invisible when not hidden`() {
        val result = computeVisibility(hidden = false, visibility = "invisible")
        assertEquals(VisibilityResult.INVISIBLE, result)
    }

    @Test
    fun `visibility visible when not hidden`() {
        val result = computeVisibility(hidden = false, visibility = "visible")
        assertEquals(VisibilityResult.VISIBLE, result)
    }

    @Test
    fun `null hidden and null visibility defaults to visible`() {
        val result = computeVisibility(hidden = null, visibility = null)
        assertEquals(VisibilityResult.VISIBLE, result)
    }

    @Test
    fun `null hidden with gone visibility`() {
        val result = computeVisibility(hidden = null, visibility = "gone")
        assertEquals(VisibilityResult.GONE, result)
    }

    // Helper functions that mirror the logic in VisibilityWrapper
    private fun shouldRender(visibility: String): Boolean {
        return visibility.lowercase() != "gone"
    }

    private fun getAlpha(visibility: String): Float {
        return when (visibility.lowercase()) {
            "invisible" -> 0f
            else -> 1f
        }
    }

    private enum class VisibilityResult {
        VISIBLE, INVISIBLE, GONE, HIDDEN
    }

    /** Mirrors VisibilityWrapper: only "gone" skips composition. */
    private fun composes(result: VisibilityResult): Boolean {
        return result != VisibilityResult.GONE
    }

    /** HIDDEN and INVISIBLE compose, so their layout space is kept. */
    private fun keepsLayoutSpace(result: VisibilityResult): Boolean {
        return composes(result)
    }

    /** Mirrors VisibilityWrapper alpha: HIDDEN/INVISIBLE draw nothing. */
    private fun alphaOf(result: VisibilityResult): Float {
        return when (result) {
            VisibilityResult.HIDDEN, VisibilityResult.INVISIBLE -> 0f
            else -> 1f
        }
    }

    /** Mirrors VisibilityWrapper: only HIDDEN clears semantics. */
    private fun hasSemantics(result: VisibilityResult): Boolean {
        return result != VisibilityResult.HIDDEN
    }

    private fun computeVisibility(hidden: Boolean?, visibility: String?): VisibilityResult {
        // Handle hidden first (takes precedence)
        if (hidden == true) {
            return VisibilityResult.HIDDEN
        }

        // Then handle visibility
        return when (visibility?.lowercase()) {
            "gone" -> VisibilityResult.GONE
            "invisible" -> VisibilityResult.INVISIBLE
            else -> VisibilityResult.VISIBLE
        }
    }
}
