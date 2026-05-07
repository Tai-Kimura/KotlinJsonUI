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

    @Test
    fun `hidden true should not render`() {
        assertTrue(shouldHide(true))
    }

    @Test
    fun `hidden false should render`() {
        assertFalse(shouldHide(false))
    }

    @Test
    fun `hidden null should render`() {
        assertFalse(shouldHide(null))
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

    private fun shouldHide(hidden: Boolean?): Boolean {
        return hidden == true
    }

    private enum class VisibilityResult {
        VISIBLE, INVISIBLE, GONE, HIDDEN
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
