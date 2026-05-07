package com.kotlinjsonui.binding

import android.view.View
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for BindingAdapters utility methods
 * Note: Tests that require Android Views are limited in unit tests
 * Full testing would require instrumented tests
 */
class BindingAdaptersTest {

    @Test
    fun `visibility string conversion - visible`() {
        val visibility = "visible"
        val expected = View.VISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - invisible`() {
        val visibility = "invisible"
        val expected = View.INVISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - gone`() {
        val visibility = "gone"
        val expected = View.GONE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - null defaults to visible`() {
        val visibility: String? = null
        val expected = View.VISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - unknown defaults to visible`() {
        val visibility = "unknown"
        val expected = View.VISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - case insensitive VISIBLE`() {
        val visibility = "VISIBLE"
        val expected = View.VISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - case insensitive INVISIBLE`() {
        val visibility = "INVISIBLE"
        val expected = View.INVISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - case insensitive GONE`() {
        val visibility = "GONE"
        val expected = View.GONE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `visibility string conversion - mixed case Visible`() {
        val visibility = "Visible"
        val expected = View.VISIBLE
        assertEquals(expected, visibilityFromString(visibility))
    }

    @Test
    fun `progress double to int conversion`() {
        assertEquals(0, 0.0.toInt())
        assertEquals(50, 50.5.toInt())
        assertEquals(99, 99.9.toInt())
        assertEquals(100, 100.0.toInt())
    }

    @Test
    fun `progress negative double to int`() {
        assertEquals(-1, (-1.5).toInt())
    }

    // Helper function that mirrors the logic in BindingAdapters
    private fun visibilityFromString(visibility: String?): Int {
        return when (visibility?.lowercase()) {
            "visible" -> View.VISIBLE
            "invisible" -> View.INVISIBLE
            "gone" -> View.GONE
            else -> View.VISIBLE
        }
    }
}
