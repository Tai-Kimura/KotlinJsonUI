package com.kotlinjsonui.views

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KjuiEditTextTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun kjuiEditText_initializes() {
        val editText = KjuiEditText(context)
        assertNotNull(editText)
    }

    @Test
    fun kjuiEditText_setsText() {
        val editText = KjuiEditText(context)
        editText.setText("Test input")
        assertEquals("Test input", editText.text.toString())
    }

    @Test
    fun kjuiEditText_setsFontWithNullName() {
        val editText = KjuiEditText(context)
        editText.setFont(null as String?)
        // Should not crash and use default typeface
        assertNotNull(editText.typeface)
    }

    @Test
    fun kjuiEditText_setsFontWithEmptyName() {
        val editText = KjuiEditText(context)
        editText.setFont("")
        // Should not crash
        assertNotNull(editText.typeface)
    }

    @Test
    fun kjuiEditText_setsFontWithInvalidName() {
        val editText = KjuiEditText(context)
        editText.setFont("nonexistent_font.ttf")
        // Should not crash, falls back to default
        assertNotNull(editText.typeface)
    }

    @Test
    fun kjuiEditText_typefaceDefaults() {
        val editText = KjuiEditText(context)
        // Default typeface should be set
        assertNotNull(editText.typeface)
    }

    @Test
    fun kjuiEditText_companionSchemaConstant() {
        assertEquals("http://schemas.android.com/apk/res/android", KjuiEditText.ANDROID_SCHEMA)
    }

    @Test
    fun kjuiEditText_isFocusable() {
        val editText = KjuiEditText(context)
        assertTrue(editText.isFocusable)
    }
}
