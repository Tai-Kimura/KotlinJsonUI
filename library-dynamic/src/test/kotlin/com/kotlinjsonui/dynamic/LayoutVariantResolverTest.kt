package com.kotlinjsonui.dynamic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Responsive variant-file name helpers (home@regular.json — 06 track).
 */
class LayoutVariantResolverTest {

    @Test
    fun `baseOf strips a variant suffix`() {
        assertEquals("home", LayoutVariantResolver.baseOf("home@regular"))
        assertEquals("shop/home", LayoutVariantResolver.baseOf("shop/home@compact"))
    }

    @Test
    fun `baseOf returns base names unchanged`() {
        assertEquals("home", LayoutVariantResolver.baseOf("home"))
        assertEquals("shop/home", LayoutVariantResolver.baseOf("shop/home"))
    }

    @Test
    fun `isVariant detects the suffix on the basename only`() {
        assertTrue(LayoutVariantResolver.isVariant("home@regular"))
        assertTrue(LayoutVariantResolver.isVariant("shop/home@medium"))
        assertFalse(LayoutVariantResolver.isVariant("home"))
        assertFalse(LayoutVariantResolver.isVariant("shop/home"))
    }

    @Test
    fun `valid classes are the v1 single-size-class vocabulary`() {
        assertEquals(listOf("compact", "medium", "regular"), LayoutVariantResolver.VALID_CLASSES)
    }
}
