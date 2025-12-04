package com.kotlinjsonui.utils

import android.content.Context
import android.graphics.Typeface
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FontCacheTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun fontCache_getTypefaceWithNull() {
        val typeface = FontCache.getTypeface(context, null)
        // Should return default typeface when null is passed
        assertEquals(Typeface.DEFAULT, typeface)
    }

    @Test
    fun fontCache_getTypefaceWithEmptyString() {
        val typeface = FontCache.getTypeface(context, "")
        // Should return default typeface when empty string is passed
        assertEquals(Typeface.DEFAULT, typeface)
    }

    @Test
    fun fontCache_getTypefaceWithInvalidFont() {
        val typeface = FontCache.getTypeface(context, "nonexistent_font.ttf")
        // Should return default typeface when font file doesn't exist
        assertEquals(Typeface.DEFAULT, typeface)
    }

    @Test
    fun fontCache_returnsSameTypefaceForSameFontName() {
        val typeface1 = FontCache.getTypeface(context, "test_font")
        val typeface2 = FontCache.getTypeface(context, "test_font")
        // Should return the same typeface object (cached)
        assertSame(typeface1, typeface2)
    }

    @Test
    fun fontCache_clearCache() {
        // Get a typeface to populate cache
        FontCache.getTypeface(context, "test_font")
        // Clear the cache
        FontCache.clearCache()
        // No exception should be thrown
    }
}
