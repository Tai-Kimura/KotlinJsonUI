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
class KjuiTextViewTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun kjuiTextView_initializes() {
        val textView = KjuiTextView(context)
        assertNotNull(textView)
    }

    @Test
    fun kjuiTextView_setsText() {
        val textView = KjuiTextView(context)
        textView.text = "Hello World"
        assertEquals("Hello World", textView.text.toString())
    }

    @Test
    fun kjuiTextView_setsFontWithNullName() {
        val textView = KjuiTextView(context)
        textView.setFont(null as String?)
        // Should not crash and use default typeface
        assertNotNull(textView.typeface)
    }

    @Test
    fun kjuiTextView_setsFontWithEmptyName() {
        val textView = KjuiTextView(context)
        textView.setFont("")
        // Should not crash
        assertNotNull(textView.typeface)
    }

    @Test
    fun kjuiTextView_setsFontWithInvalidName() {
        val textView = KjuiTextView(context)
        textView.setFont("nonexistent_font.ttf")
        // Should not crash, falls back to default
        assertNotNull(textView.typeface)
    }

    @Test
    fun kjuiTextView_typefaceDefaults() {
        val textView = KjuiTextView(context)
        // Default typeface should be set
        assertNotNull(textView.typeface)
    }

    @Test
    fun kjuiTextView_companionSchemaConstant() {
        assertEquals("http://schemas.android.com/apk/res/android", KjuiTextView.ANDROID_SCHEMA)
    }
}
