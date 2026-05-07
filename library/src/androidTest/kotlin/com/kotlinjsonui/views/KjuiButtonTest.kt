package com.kotlinjsonui.views

import android.content.Context
import android.graphics.Typeface
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KjuiButtonTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun kjuiButton_initializes() {
        val button = KjuiButton(context)
        assertNotNull(button)
    }

    @Test
    fun kjuiButton_setsText() {
        val button = KjuiButton(context)
        button.text = "Click Me"
        assertEquals("Click Me", button.text.toString())
    }

    @Test
    fun kjuiButton_setsFontWithNullName() {
        val button = KjuiButton(context)
        button.setFont(null as String?)
        // Should not crash and use default typeface
        assertNotNull(button.typeface)
    }

    @Test
    fun kjuiButton_setsFontWithEmptyName() {
        val button = KjuiButton(context)
        button.setFont("")
        // Should not crash
        assertNotNull(button.typeface)
    }

    @Test
    fun kjuiButton_setsFontWithInvalidName() {
        val button = KjuiButton(context)
        button.setFont("nonexistent_font.ttf")
        // Should not crash, falls back to default
        assertNotNull(button.typeface)
    }

    @Test
    fun kjuiButton_typefaceDefaults() {
        val button = KjuiButton(context)
        // Default typeface should be set
        assertNotNull(button.typeface)
    }

    @Test
    fun kjuiButton_companionSchemaConstant() {
        assertEquals("http://schemas.android.com/apk/res/android", KjuiButton.ANDROID_SCHEMA)
    }

    @Test
    fun kjuiButton_isClickable() {
        val button = KjuiButton(context)
        assertTrue(button.isClickable)
    }
}
