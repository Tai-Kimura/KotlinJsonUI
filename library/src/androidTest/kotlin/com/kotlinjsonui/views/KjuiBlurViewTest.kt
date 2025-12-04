package com.kotlinjsonui.views

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KjuiBlurViewTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun kjuiBlurView_initializes() {
        val view = KjuiBlurView(context)
        assertNotNull(view)
    }

    @Test
    fun kjuiBlurView_canAddChildViews() {
        val view = KjuiBlurView(context)
        val child = android.widget.TextView(context)
        view.addView(child)
        assertEquals(1, view.childCount)
    }

    @Test
    fun kjuiBlurView_isFrameLayout() {
        val view = KjuiBlurView(context)
        assertTrue(view is android.widget.FrameLayout)
    }
}
