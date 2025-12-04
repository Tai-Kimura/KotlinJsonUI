package com.kotlinjsonui.utils

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for FontCache
 * Note: Tests that require Android Context are limited in unit tests
 * Full testing with Context would require instrumented tests
 */
class FontCacheTest {

    @Before
    fun setUp() {
        // Clear the cache before each test
        FontCache.clear()
    }

    @Test
    fun `clear method does not throw exception`() {
        // This test verifies that clear() can be called without errors
        assertDoesNotThrow {
            FontCache.clear()
        }
    }

    @Test
    fun `clear can be called multiple times`() {
        assertDoesNotThrow {
            FontCache.clear()
            FontCache.clear()
            FontCache.clear()
        }
    }

    private fun assertDoesNotThrow(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            fail("Expected no exception but got: ${e.message}")
        }
    }
}
